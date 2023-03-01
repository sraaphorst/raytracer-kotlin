package scene

// By Sebastian Raaphorst, 2023.

import math.Color
import math.DEFAULT_PRECISION
import math.cartesianProduct
import output.Canvas
import java.util.*
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.roundToInt

private typealias CanvasMap = Array<Array<Boolean>>

sealed class AntiAliasing {
    abstract fun render(world: World, camera: Camera): Canvas

    object NoAntiAliasing : AntiAliasing() {
        override fun render(world: World, camera: Camera): Canvas {
            val image = Canvas(camera.hSize, camera.vSize)
            (0 until camera.vSize).forEach { y ->
                (0 until camera.hSize).toList().parallelStream().forEach { x ->
                    val ray = camera.rayForPixel(x, y)
                    val color = world.colorAt(ray)
                    image[x, y] = color
                }
            }

            return image
        }
    }

    object BlurAntiAliasing : AntiAliasing() {
        override fun render(world: World, camera: Camera): Canvas {
            val originalImage = NoAntiAliasing.render(world, camera)

            val image = Canvas(camera.hSize, camera.vSize)
            (0 until camera.vSize).forEach { y ->
                (0 until camera.hSize).toList().parallelStream().forEach { x ->
                    // Determine the pixels for this component.
                    val offsets1D = listOf(-1, 0, 1)
                    val pixels = offsets1D.cartesianProduct(offsets1D).map { (dx, dy) ->
                        x + dx to y + dy
                    }.filter { (px, py) -> px in (0 until camera.hSize) && py in (0 until camera.vSize) }

                    image[x, y] = pixels.map { (px, py) ->
                        originalImage[px, py]
                    }.reduce { c1, c2 -> c1 + c2 } / pixels.size
                }
            }
            return image
        }
    }

    class SuperScaleAntiAliasing(private val factor: Int) : AntiAliasing() {
        override fun render(world: World, camera: Camera): Canvas  {
            // Make a super camera to capture the larger image.
            val superCamera = AntiAliasing.superScaleCanvas(camera, factor)

            // Get the larger image.
            val largerImage = NoAntiAliasing.render(world, superCamera)

            // Shrink it down.
            val deltas1D = (0 until factor).toList()
            val deltas = deltas1D.cartesianProduct(deltas1D)

            // Each factor x factor square of pixels shrinks down to a single pixel.
            val factor2 = factor * factor
            val image = Canvas(camera.hSize, camera.vSize)

            (0 until camera.vSize).forEach { y ->
                (0 until camera.hSize).toList().parallelStream().forEach { x ->
                    val pixels = deltas.map { (dx, dy) -> largerImage[factor * x + dx, factor * y + dy] }
                    image[x, y] = pixels.reduce { c1, c2 -> c1 + c2 } / factor2
                }
            }
            return image
        }
    }

    class DistributedAntiAliasing(private val factor: Int = 4, private val numRays: Int = 4) : AntiAliasing() {
        override fun render(world: World, camera: Camera): Canvas {
            // Create a canvas of a larger size, pick rays to trace from the multiplication
            // factor, and then combine them through averaging.
            val superScaleCamera = superScaleCanvas(camera, factor)

            // In the end, we want a regular canvas.
            val image = Canvas(camera.hSize, camera.vSize)

            // For each pixel of the regular canvas, pick numRays random pixels from the larger
            // image, trace them, and then combine them by averaging to set the image.
            (0 until camera.vSize).forEach { y ->
                (0 until camera.hSize).toList().parallelStream().forEach { x ->
                    // Make a list of corresponding pixels and shuffle them.
                    val colChoices = (0 until factor).map { factor * y + it }
                    val rowChoices = (0 until factor).map { factor * x + it }
                    val color = rowChoices.cartesianProduct(colChoices)
                        .asSequence()
                        .shuffled()
                        .take(numRays)
                        .map { (r, c) -> world.colorAt(superScaleCamera.rayForPixel(r, c)) }
                        .toList()
                        .reduce { c1, c2 -> c1 + c2} / numRays
                    image[x, y] = color
                }
            }

            return image
        }
    }

    class AdaptiveAntiAliasing(private val factor: Int) : AntiAliasing() {
        override fun render(world: World, camera: Camera): Canvas {
            // Idea: for each pixel, cast rays out for the pixels, If the differences between the corners
            // is nearly none, then take the value as the pixel value.
            // If, on the other hand, the corner differences are large, we recurse.
            // https://web.cs.wpi.edu/~matt/courses/cs563/talks/antialiasing/adaptive.html
            // https://courses.cs.washington.edu/courses/csep557/01sp/lectures/aa-and-drt.pdf
            // ChatGPT:
            /**
             * In this pseudocode, renderScene is a function that renders the entire scene at a low resolution
             * using a specified number of rays per pixel. renderPixel is a function that renders a single pixel
             * at a higher resolution, using a specified number of rays per pixel. blendPixels is a function that
             * blends a high resolution pixel with the corresponding low resolution pixel, taking into account the
             * amount of detail in each region. finalizeImage is a function that applies any final post-processing
             * steps to the image, such as gamma correction or tone mapping.
             */
            /**
             * From Craddick, T: Improved ray tracing performance through tri-adaptive sampling, MSc Project, 2023:
             * https://scholarworks.alaska.edu/bitstream/handle/11122/11862/Craddick_T_2020.pdf
             *
             * The basic premise involves first rendering the scene at a
             * low resolution, some power of two fractionally lower than the target resolution. This lower
             * resolution represents the under-sampled state of the scene render, where one ray represents
             * multiple pixels of the render at the target resolution.
             * Information about the scene is collected from the render at a given resolution, ranging
             * from information about where and how the ray hit the closest object, to the color of the
             * resulting rendered image. This data is stored into a batch of individual textures matching
             * the respective resolution. These texture files are then upscaled to match the next higher
             * scene resolution.
             * At this new resolution tier, the renderer samples the textures of the previous resolution in
             * order to determine whether to shoot new rays at the current resolution. This is accomplished
             * by comparing the values of the textures respective to the current pixel with the values of
             * the neighboring pixels. If there is a significant difference between the current pixel and
             * the neighboring pixels, then it is assumed that something of interest is occurring within
             * the local pixel block, and new rays should be shot at the current resolution. Conversely, if
             * there is little difference between the current pixel and its neighbors, then there is no need
             * to shoot new rays, and the results from the lower resolution render can be taken for the
             * current resolution. This trace versus sample structure is what composes the core of the
             * multi-resolution approach.
             * This process continues until the target resolution has been met, at which point all undersampling
             * has been accomplished.
             */

            // Create the default canvas.
            val superScaleCamera = superScaleCanvas(camera, factor)

            // For each pixel, shoot out this many rays.
            val l2f = log2(factor.toDouble())
            if (abs(l2f - l2f.roundToInt()) > DEFAULT_PRECISION || factor < 2)
                throw IllegalArgumentException("Factor $factor is not appropriate for adaptive anti-aliasing.")

            val image = Canvas(camera.hSize, camera.vSize)
            val imageCalculated = Array(camera.hSize) { Array(camera.vSize) {false} }

            fun renderSubsection(startX: Int, startY: Int, endX: Int, endY: Int, stepSize: Int) {
                (startY..endY step stepSize).forEach { y ->
                    (startX .. endX step stepSize).forEach { x ->
                        // If already calculated, don't recalculate.
                        if (!imageCalculated[x][y]) {
                            val t = imageCalculated[x]
                            imageCalculated[x][y] = true
                            val ray = camera.rayForPixel(x, y)
                            val color = world.colorAt(ray)
                            image[x, y] = color
                        }
                    }
                }
            }

            // Begin by calculating
            TODO()
        }
    }

    private companion object {
        private fun superScaleCanvas(camera: Camera, factor: Int): Camera =
            Camera(
                factor * camera.hSize,
                factor * camera.vSize,
                camera.fov,
                camera.transformation
            )
    }
}
