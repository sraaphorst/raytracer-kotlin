package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple
import kotlin.math.sqrt

class RingPattern(val colors: List<Color>, transformation: Matrix = Matrix.I): Pattern(transformation) {
    constructor(color1: Color, color2: Color, transformation: Matrix = Matrix.I):
            this(listOf(color1, color2), transformation)

    override fun colorAt(worldPoint: Tuple): Color =
        colors[sqrt(worldPoint.x * worldPoint.x + worldPoint.z * worldPoint.z).toInt() % colors.size]
}
