package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple
import kotlin.math.floor

class CheckerPattern(val color1: Color, val color2: Color, transformation: Matrix = Matrix.I)
    : Pattern(transformation) {
    override fun colorAt(worldPoint: Tuple): Color =
        if ((floor(worldPoint.x) + floor(worldPoint.y) + floor(worldPoint.z)).toInt() % 2 == 0)
            color1
        else
            color2
}