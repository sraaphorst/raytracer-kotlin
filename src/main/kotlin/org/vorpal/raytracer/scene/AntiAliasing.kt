package org.vorpal.raytracer.scene

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.math.Color
import org.vorpal.raytracer.math.DEFAULT_PRECISION
import org.vorpal.raytracer.math.averageColor
import org.vorpal.raytracer.math.cartesianProduct
import org.vorpal.raytracer.output.Canvas
import org.vorpal.raytracer.math.partialShuffle
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.roundToInt

private typealias CanvasMap = Array<Array<Boolean>>


sealed class AntiAliasing {
    abstract fun render(world: World, camera: Camera): Canvas

    object NoAntiAliasing : AntiAliasing() {
        override fun render(world: World, camera: Camera): Canvas {
            val image = Canvas(camera.hSize, camera.vSize)

            fun renderPixel(x: Int, y: Int) {
                val ray = camera.rayForPixel(x, y)
                val color = world.colorAt(ray)
                image[x, y] = color
            }

//             Render.forEachPixelParallelStream(camera.hSize, camera.vSize, ::renderPixel)
            Render.parallelForEachPixel(camera.hSize, camera.vSize, ::renderPixel)
            return image
        }
    }

    object BlurAntiAliasing : AntiAliasing() {
        // Create a square grid of offsets around a central point, (0, 0).
        private val offsets = listOf(-1, 0, 1).let { it.cartesianProduct(it) }

        override fun render(world: World, camera: Camera): Canvas {
            val originalImage = NoAntiAliasing.render(world, camera)
            val image = Canvas(camera.hSize, camera.vSize)

            fun renderPixel(x: Int, y: Int) {
                // Determine the pixels needed to blur together for this pixel.
                image[x, y] = offsets.map { (dx, dy) -> dx + x to dy + y }
                    .filter { (px, py) -> px in (0 until camera.hSize) && py in (0 until camera.vSize) }
                    .averageColor { (px, py) -> originalImage[px, py] }
            }

//             Render.forEachPixelParallelStream(camera.hSize, camera.vSize, ::renderPixel)
            Render.parallelForEachPixel(camera.hSize, camera.vSize, ::renderPixel)
            return image
        }
    }

    class SuperScaleAntiAliasing(private val factor: Int = 4) : AntiAliasing() {
        private val deltas = (0 until factor).let { it.cartesianProduct(it) }

        override fun render(world: World, camera: Camera): Canvas {
            // Make a super camera to capture the larger image.
            val superCamera = superScaleCanvas(camera, factor)

            // Get the larger image.
            val largerImage = NoAntiAliasing.render(world, superCamera)

            // Each factor x factor square of pixels shrinks down to a single pixel.
            val factor2 = factor * factor
            val image = Canvas(camera.hSize, camera.vSize)

            fun renderPixel(x: Int, y: Int) {
                val pixels = deltas.map { (dx, dy) -> largerImage[factor * x + dx, factor * y + dy] }
                image[x, y] = pixels.reduce { c1, c2 -> c1 + c2 } / factor2
            }

//             Render.forEachPixelParallelStream(camera.hSize, camera.vSize, ::renderPixel)
            Render.parallelForEachPixel(camera.hSize, camera.vSize, ::renderPixel)
            return image
        }
    }



    class DistributedAntiAliasing(private val factor: Int = 4, private val numRays: Int = 4) : AntiAliasing() {
        // To speed things up, pre-prepare these.
        private val factor2 = factor * factor

        override fun render(world: World, camera: Camera): Canvas {
            // Create a canvas of a larger size, pick rays to trace from the multiplication
            // factor, and then combine them through averaging.
            val superScaleCamera = superScaleCanvas(camera, factor)

            // In the end, we want a regular canvas.
            val image = Canvas(camera.hSize, camera.vSize)

            // For each pixel of the regular canvas, pick numRays random pixels from the larger
            // image, trace them, and then combine them by averaging to set the image.
            fun renderPixel(x: Int, y: Int) {
                val rowBase = factor * x
                val colBase = factor * y
                val indices = MutableList(factor2) { it }.partialShuffle(numRays)
                var pixel = Color.BLACK
                for (i in 0 until numRays) {
                    val idx = indices[i]
                    val r = rowBase + idx / factor
                    val c = colBase + idx % factor
                    pixel += world.colorAt(superScaleCamera.rayForPixel(r, c, true))
                }
                image[x, y] = pixel / numRays
            }

//             Render.forEachPixelParallelStream(camera.hSize, camera.vSize, ::renderPixel)
            Render.parallelForEachPixel(camera.hSize, camera.vSize, ::renderPixel)
            return image
        }
    }

    class AdaptiveAntiAliasing(private val factor: Int = 4, private val tolerance: Double = 1e-2) : AntiAliasing() {
        // Idea: for a grid of pixels, cast rays out for the pixels, If the differences between pixels
        // in the same grid region is greater than the tolerance in some color value, then split that
        // square in the grid into four squares by casting a ray out for the central point.
        // If, on the other hand, the corner differences are large, we recurse.
        // https://courses.cs.washington.edu/courses/csep557/01sp/lectures/aa-and-drt.pdf

        // First, check that the factor is a power of 2.
        private val l2f = log2(factor.toDouble())
        private val l2fInt = l2f.roundToInt()
        init {
            if (abs(l2f - l2fInt) > DEFAULT_PRECISION || factor < 2)
                throw IllegalArgumentException("Factor $factor is not a positive power of 2.")
        }

        override fun render(world: World, camera: Camera): Canvas {
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
                // Render sparsely sampled grid: a square in the larger image, and subdivide as necessary.
                for (y in startY..endY step delta) {
                    for (x in startX..endX step delta) {
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
                    val newDelta = delta / 2
                    for (y in startY..(endY - delta) step delta) {
                        for (x in startX..(endX - delta) step delta) {
                            // Upper left is (x, y).
                            // Upper right is (x, y + delta).
                            // Lower left is (x + delta, y).
                            // Lower right is (x + delta, y + delta).
                            // Center point is (x + newDelta, y + newDelta)
                            // Check all six possible combinations of points for UL, UR, LL, LR to see
                            // if the tolerance is exceeded, in which case we subdivide.
                            if (subdivide(x, y, x, y + delta) ||
                                subdivide(x, y, x + delta, y) ||
                                subdivide(x, y + delta, x + delta, y + delta) ||
                                subdivide(x + delta, y, x + delta, y + delta) ||
                                subdivide(x, y, x + delta, y + delta) ||
                                subdivide(x + delta, y, x, y + delta)
                            ) {
                                // Four recursive quadrants.
                                renderSubsection(x, y, x + newDelta, y + newDelta, newDelta)
                                renderSubsection(x, y + newDelta, x + newDelta, y + delta, newDelta)
                                renderSubsection(x + newDelta, y, x + delta, y + newDelta, newDelta)
                                renderSubsection(x + newDelta, y + newDelta, x + delta, y + delta, newDelta)
                            }
                        }
                    }
                }
            }

            // We begin by rendering the corners in the larger pixel and traverse down as needed.
            renderSubsection(0, 0, factor * camera.hSize, factor * camera.vSize, factor)

            fun renderRows(yStart: Int, yEnd: Int): Unit {
                for (y in yStart until yEnd) {
                    for (x in 0 until camera.hSize) {
                        // Get all the points in the larger canvas that correspond to these.
                        // The points are in the range:
                        // [x * factor, (x + 1) * factor] X [y * factor, (y + 1) * factor]
                        val points = ((x * factor)..((x + 1) * factor)).flatMap { xp ->
                            ((y * factor)..((y + 1) * factor))
                                .map { yp -> largerImage[xp, yp] }
                                .filter { it != Canvas.NO_COLOR }
                        }

                        if (points.isNotEmpty()) {
                            val sum = points.reduce { c1, c2 -> c1 + c2 }
                            image[x, y] = sum / points.size
                        }
                    }
                }
            }

            // Now combine the sections by gathering all the points and averaging them out.
//            Render.forEachRowParallelStream(camera.vSize, ::renderRows)
            Render.parallelForEachRowChunk(camera.vSize, rowsPerChunk = 8, ::renderRows)

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
