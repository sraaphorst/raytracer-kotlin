package scene

// By Sebastian Raaphorst, 2023.

import math.Matrix
import math.Ray
import math.Tuple
import output.Canvas
import kotlin.math.tan

import math.cartesianProduct


// Map a 3D scene onto a 2D canvas.
class Camera(internal val hSize: Int,
             internal val vSize: Int,
             internal val fov: Number,
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
    fun render(world: World, antialiasing: AntiAliasing = NoAntiAliasing()): Canvas =
        antialiasing.render(world, this)
}

abstract class AntiAliasing {
    abstract fun render(world: World, camera: Camera): Canvas
}


class NoAntiAliasing: AntiAliasing() {
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

class BlurAntiAliasing: AntiAliasing() {
    override fun render(world: World, camera: Camera): Canvas {
        val originalImage  = NoAntiAliasing().render(world, camera)

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
                }.reduce { c1, c2 -> c1 + c2} / pixels.size
            }
        }
        return image
    }
}

class SuperScaleAntiAliasing(private val factor: Int): AntiAliasing() {
    override fun render(world: World, camera: Camera): Canvas {
        // Make a super camera to capture the larger image.
        val superCamera = Camera(
            factor * camera.hSize,
            factor * camera.vSize,
            camera.fov,
            camera.transformation
        )

        // Get the larger image.
        val largerImage = NoAntiAliasing().render(world, superCamera)

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
