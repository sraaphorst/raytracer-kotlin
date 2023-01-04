package apps

// By Sebastian Raaphorst, 2023.

import math.hit
import math.Color
import math.Ray
import math.Tuple
import output.Canvas
import shapes.Sphere
import java.io.File

fun main() {
    val size = 1000

    // Start the ray at z = -5.
    val rayOrigin = Tuple.point(0, 0, -5)

    // Put the wall at z = 10.
    val wallZ = 10.0
    val wallSize = 7.0

    // Calculate the pixel size.
    val pixelSize = wallSize / size

    // As the sphere is centred at the origin, this determines min / max values.
    val half = wallSize / 2

    // Create the canvas, sphere, and the color.
    val canvas = Canvas(size, size)
    val color = Color.RED
    val shape = Sphere()

    // For each row of pixels in the canvas:
    (0 until size).forEach { y ->
        // Compute the world y coordinate (top = +half, bottom = -half).
        val worldY = half - pixelSize * y

        // For each pixel in the row:
        (0 until size).toList().parallelStream().forEach { x ->
            // Compute the world x coordinate (left = -half, right = -half).
            val worldX = -half + pixelSize * x

            // Describe the point on the wall that the ray will target.
            val position = Tuple.point(worldX, worldY, wallZ)
            val ray = Ray(rayOrigin, (position - rayOrigin).normalized)
            val xs = shape.intersect(ray)

            if (xs.hit() != null)
                canvas[x, y] = color
        }
    }

    canvas.toPPMFile(File("ch05_sphere.ppm"))
}
