package apps

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple
import output.Canvas
import java.io.File
import java.time.LocalTime
import kotlin.math.PI

fun main() {
    val size = 300
    val border = 50
    val pointColor = Color(1, 1, 0)
    val hourColor = Color.RED
    val minuteColor = Color(0.5, 0.5, 1)
    val hourAngle = PI / 6
    val minuteAngle = PI / 30

    // Scale to the desired height.
    val sz = (size - 2 * border) / 2
    val sc = Matrix.scale(sz, sz, 0)

    // Translation to get to the middle of the canvas.
    val tr = Matrix.translate(size / 2, size / 2, 0)

    // Create the canvas.
    val canvas = Canvas(size, size)

    // Draw the hour points of the clock.
    val p = Tuple.point(0, -1, 0)
    (0 until 12).forEach {
        val newP = Matrix.rotationZ(it * hourAngle)
            .andThen(sc)
            .andThen(tr) * p
        canvas[newP.x, newP.y] = pointColor
    }

    // Add the hands to show the current time.
    val time = LocalTime.now()
    val hour = time.hour % 12
    val min = time.minute

    // Draw the hour hand.
    (-10 until sz - 20).forEach {
        val newP = Matrix.rotationZ((hour + min / 60.0) * hourAngle)
            .andThen(Matrix.scale(it, it, it))
            .andThen(tr) * p
        canvas[newP.x, newP.y] = hourColor
    }

    // Draw the minute hand.
    (-10 until sz).forEach {
        val newP = Matrix.rotationZ(min * minuteAngle)
            .andThen(Matrix.scale(it, it, it))
            .andThen(tr) * p
        canvas[newP.x, newP.y] = minuteColor
    }

    canvas.toPPMFile(File("output/ch04_clock.ppm"))
}
