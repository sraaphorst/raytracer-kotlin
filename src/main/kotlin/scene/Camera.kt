package scene

// By Sebastian Raaphorst, 2023.

import math.Matrix
import math.Ray
import math.Tuple
import output.Canvas
import kotlin.math.tan

import math.cartesianProduct


enum class Antialiasing {
    NONE,
    BLUR
}

// Map a 3D scene onto a 2D canvas.
class Camera(private val hSize: Int,
             private val vSize: Int,
             fov: Number,
             internal val transformation: Matrix = Matrix.I) {
    init {
        if (!transformation.isTransformation())
            throw IllegalArgumentException("Illegal camera transformation:\n${transformation.show()}")
    }

    private val halfView = tan(fov.toDouble() / 2.0)
    private val aspect = hSize.toDouble() / vSize.toDouble()
    private val halfWidth = if (aspect >= 1) halfView else halfView * aspect
    private val halfHeight = if (aspect >= 1) halfView / aspect else halfView
    val pixelSize = 2 * halfWidth / hSize

    fun rayForPixel(px: Int, py: Int): Ray {
        // Calculate offset from edge of canvas to pixel's centre.
        val xOffset = (px + 0.5) * pixelSize
        val yOffset = (py + 0.5) * pixelSize

        // The untransformed coordinates of the pixel in world space.
        // Remember that the camera looks towards -z, so +x is to the left.
        val worldX = halfWidth - xOffset
        val worldY = halfHeight - yOffset

        // Using the camera matrix, transform the canvas point and origin,
        // and then compute the ray's direction vector.
        // Remember that the canvas is at z = -1.
        val pixel = transformation.inverse * Tuple.point(worldX, worldY, -1)
        val origin = transformation.inverse * Tuple.PZERO
        val direction = (pixel - origin).normalized

        return Ray(origin, direction)
    }

    // Render the world with the camera.
    // Computation is parallelized.
    fun render(world: World, antialiasing: Antialiasing = Antialiasing.NONE): Canvas {
        val image = Canvas(hSize, vSize)
        (0 until vSize).forEach { y ->
            (0 until hSize).toList().parallelStream().forEach { x ->
                val ray = rayForPixel(x, y)
                val color = world.colorAt(ray)
                image[x, y] = color
            }
        }

        // If we are doing incredibly simple anti-aliasing, we create a new Image where each pixel
        // is the average of itself and the surrounding pixels.
        if (antialiasing == Antialiasing.BLUR) {
            val aaImage = Canvas(hSize, vSize)
            (0 until vSize).forEach { y ->
                (0 until hSize).toList().parallelStream().forEach { x ->
                    // Determine the pixels for this component.
                    val offsets1D = listOf(-1, 0, 1)
                    val pixels = offsets1D.cartesianProduct(offsets1D).map { (dx, dy) ->
                        x + dx to y + dy
                    }.filter { (px, py) -> px in (0 until hSize) && py in (0 until vSize) }

                    aaImage[x, y] = pixels.map { (px, py) ->
                        image[px, py]
                    }.reduce { c1, c2 -> c1 + c2} / pixels.size
                }
            }
            return aaImage
        }
        return image
    }
}
