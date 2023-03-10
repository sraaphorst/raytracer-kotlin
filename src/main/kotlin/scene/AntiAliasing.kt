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

    class SuperScaleAntiAliasing(private val factor: Int = 4) : AntiAliasing() {
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
                        .map { (r, c) -> world.colorAt(superScaleCamera.rayForPixel(r, c, true)) }
                        .toList()
                        .reduce { c1, c2 -> c1 + c2} / numRays
                    image[x, y] = color
                }
            }

            return image
        }
    }

    class AdaptiveAntiAliasing(private val factor: Int = 4, private val tolerance: Double = 1e-2) : AntiAliasing() {
        override fun render(world: World, camera: Camera): Canvas {
            // Idea: for a grid of pixels, cast rays out for the pixels, If the differences between pixels
            // in the same grid region is greater than the tolerance in some color value, then split that
            // square in the grid into four squares by casting a ray out for the central point.
            // If, on the other hand, the corner differences are large, we recurse.
            // https://courses.cs.washington.edu/courses/csep557/01sp/lectures/aa-and-drt.pdf

            // First, check that the factor is a power of 2.
            val l2f = log2(factor.toDouble())
            val l2fInt = l2f.roundToInt()
            if (abs(l2f - l2fInt) > DEFAULT_PRECISION || factor < 2)
                throw IllegalArgumentException("Factor $factor is not a positive power of 2.")

            // The super scale camera for the most resolution. This is atypical since we need to
            // multiply by factor and add one since we will be sharing corners between pixels.
            // Example: a 1x3 view, blown up to (4 * 1 + 1)x(4 * 3 + 1) = 5x13.
            // for a factor of 4 could go to:
            // x___x___x___x
            // __x_x________
            // ___xx________
            // x_xxx___x___x
            val superScaleCamera = Camera(
                factor * camera.hSize + 1,
                factor * camera.vSize + 1,
                camera.fov,
                camera.transformation
            )

            // Create the canvas for the larger image and mark it uncolored.
            val largerImage = Canvas(factor * camera.hSize + 1, factor * camera.vSize + 1)
            largerImage.clear(Canvas.NO_COLOR)

            // Create the camera for the image, which must be (factor * size + 1) so we can do shared corners
            // between pixels.
            val image = Canvas(camera.hSize, camera.vSize)

            // Check the tolerance between two pixels to see if they are far enough apart
            // to justify subdivision. We need to check each color independently.
            fun subdivide(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
                val diff = largerImage[x1, y1] - largerImage[x2, y2]
                return abs(diff.r) > tolerance || abs(diff.g) > tolerance || abs(diff.b) > tolerance
            }

            // Render a subsection of the larger image.
            fun renderSubsection(startX: Int, startY: Int, endX: Int, endY: Int, delta: Int) {
                // We will render a square in the larger image and subdivide as necessary.
                (startY..endY step delta).forEach { y ->
                    (startX..endX step delta).toList().parallelStream().forEach { x ->
                        // Render pixel (x, y) in the larger image if it has not already been rendered.
                        if (largerImage[x, y] == Canvas.NO_COLOR)
                            largerImage[x, y] = world.colorAt(superScaleCamera.rayForPixel(x, y))
                    }
                }

                // Now check to see if there is enough difference between the pixels to subdivide.
                // As per the diagram below, we have the colors for the points marked a, b, c, d.
                // Check the corner points we just calculated and if any of them result in subdivisions,
                // calculate the four subdivisions determined by the points marked * and recurse,
                // provided delta > 1 (for if delta == 1, we are at maximum resolution).
                // a _ * _ b
                // _ 1 _ 2 _
                // * _ * _ *
                // _ 3 _ 4 _
                // c _ * _ d
                if (delta > 1) {
                (startY..(endY-delta) step delta).forEach { y ->
                    (startX..(endX-delta) step delta).toList().parallelStream().forEach { x ->
                        // Upper left is (x, y).
                        // Upper right is (x, y + delta).
                        // Lower left is (x + delta, y).
                        // Lower right is (x + delta, y + delta).
                        // Center point is (x + newDelta, y + newDelta)
                        val newDelta = delta / 2

                        // Check all six possible combinations of points for UL, UR, LL, LR to see
                        // if the tolerance is exceeded, in which case we subdivide.
                        if (subdivide(x, y, x, y + delta) ||
                            subdivide(x, y, x + delta, y) ||
                            subdivide(x, y + delta, x + delta, y + delta) ||
                            subdivide(x + delta, y, x + delta, y + delta) ||
                            subdivide(x, y, x + delta, y + delta) ||
                            subdivide(x + delta, y, x, y + delta)) {
                                // Subsection 1
                                renderSubsection(x, y, x + newDelta, y + newDelta, newDelta)
                                // Subsection 2
                                renderSubsection(x, y + newDelta, x + newDelta, y + delta, newDelta)
                                // Subsection 3
                                renderSubsection(x + newDelta, y, x + delta, y + newDelta, newDelta)
                                // Subsection 4
                                renderSubsection(x + newDelta, y + newDelta,
                                    x + delta, y + delta, newDelta)
                            }
                        }
                    }
                }
            }

            // We begin by rendering the corners in the larger pixel and traverse down as needed.
            renderSubsection(0, 0, factor * camera.hSize, factor * camera.vSize, factor)

            // Now combine the sections by gathering all the points and averaging them out.
            (0 until camera.vSize).forEach { y ->
                (0 until camera.hSize).toList().parallelStream().forEach { x ->
                    // Get all the points in the larger canvas that correspond to these.
                    // The points are in the range:
                    // [x * factor, (x + 1) * factor] X [y * factor, (y + 1) * factor]
                    val points = ((x * factor)..((x + 1) * factor)).flatMap { xp ->
                        ((y * factor)..((y + 1) * factor)).map { yp -> largerImage[xp, yp] }
                            .filter { it != Canvas.NO_COLOR }
                    }
                    image[x, y] = points.reduce { c1, c2 -> c1 + c2 } / points.size
                }
            }

            return image
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
