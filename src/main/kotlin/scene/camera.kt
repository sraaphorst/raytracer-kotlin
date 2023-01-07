package scene

// By Sebastian Raaphorst, 2023.

import math.Matrix
import math.Ray
import math.Tuple
import output.Canvas
import kotlin.math.tan

// Map a 3D scene onto a 2D canvas.
data class Camera(val hSize: Int, val vSize: Int, val fov: Double, val transform: Matrix = Matrix.I) {
    init {
        if (transform.m != 4 || transform.n != 4)
            throw IllegalArgumentException("Illegal camera transformation:\n${transform.show()}")
    }

    private val halfView = tan(fov / 2.0)
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
        val pixel = transform.inverse * Tuple.point(worldX, worldY, -1)
        val origin = transform.inverse * Tuple.PZERO
        val direction = (pixel - origin).normalized

        return Ray(origin, direction)
    }

    // Render the world with the camera.
    // Computation is parallelized.
    fun render(world: World): Canvas {
        val image = Canvas(hSize, vSize)
        (0 until vSize).forEach { y ->
            (0 until hSize).toList().stream().forEach { x ->
                val ray = rayForPixel(x, y)
                val color = world.colorAt(ray)
                image[x, y] = color
            }
        }
        return image
    }
}
