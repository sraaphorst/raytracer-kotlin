package scene

// By Sebastian Raaphorst, 2023.

import math.Matrix
import math.Ray
import math.Tuple
import output.Canvas
import kotlin.math.tan
import kotlin.random.Random


// Map a 3D scene onto a 2D canvas.
class Camera(
    internal val hSize: Int,
    internal val vSize: Int,
    internal val fov: Number,
    internal val transformation: Matrix = Matrix.I,
) {
    init {
        if (!transformation.isTransformation())
            throw IllegalArgumentException("Illegal camera transformation:\n${transformation.show()}")
    }

    private val halfView = tan(fov.toDouble() / 2.0)
    private val aspect = hSize.toDouble() / vSize.toDouble()
    private val halfWidth = if (aspect >= 1) halfView else halfView * aspect
    private val halfHeight = if (aspect >= 1) halfView / aspect else halfView
    val pixelSize = 2 * halfWidth / hSize

    fun rayForPixel(px: Int, py: Int, jitter: Boolean = false): Ray {
        // Calculate offset from edge of canvas to pixel's centre.
        val xOffset = (px + 0.5) * pixelSize
        val yOffset = (py + 0.5) * pixelSize

        // For distributed ray tracing, we want to allow some jittering.
        val xJitter = if (jitter) Random.nextDouble(-0.5, 0.5) * pixelSize else 0.0
        val yJitter = if (jitter) Random.nextDouble(-0.5, 0.5) * pixelSize else 0.0

        // The untransformed coordinates of the pixel in world space.
        // Remember that the camera looks towards -z, so +x is to the left.
        val worldX = halfWidth - xOffset + xJitter
        val worldY = halfHeight - yOffset + yJitter

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
    fun render(world: World, antialiasing: AntiAliasing = AntiAliasing.NoAntiAliasing): Canvas =
        antialiasing.render(world, this)
}
