package exec

// By Sebastian Raaphorst, 2022.

import math.Color
import math.Tuple
import output.Canvas
import java.io.File
import kotlin.math.roundToInt

fun main() {
    // Enlarge each position to a size x size square so that it is more visible.
    val size = 5

    // Use an orange shade of color.
    val color = Color(1, 0.5, 0)

    val start = Tuple.point(0, 1, 0)
    val velocity = Tuple.vector(1, 1.8, 0).normalized * 11.25
    val projectile = Projectile(start, velocity)

    val gravity = Tuple.vector(0, -0.1, 0)
    val wind = Tuple.vector(-0.01, 0, 0)
    val environment = Environment(gravity, wind)

    val canvas = Canvas(900, 555)

    val sequence = generateSequence(projectile) { tick(environment, it) }
        .map { it.position }
        .takeWhile { it.y >= 0 }

    sequence.forEach {
        val x = it.x.roundToInt()
        val y = 550 - it.y.roundToInt()
        (0..size).forEach { deltac -> (0..size).forEach { deltar ->
            canvas.writePixel(x + deltac, y + deltar, color)
        } }
    }
    canvas.toPPMFile(File("cannonrender.ppm"))
}