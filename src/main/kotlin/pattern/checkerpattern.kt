package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple
import kotlin.math.floor

class CheckerPattern(val pattern1: Pattern, val pattern2: Pattern, transformation: Matrix = Matrix.I)
    : Pattern(transformation) {
    constructor(color1: Color, color2: Color, transformation: Matrix = Matrix.I):
            this(SolidPattern(color1), SolidPattern(color2), transformation)

    override fun colorAt(worldPoint: Tuple): Color =
        if ((floor(worldPoint.x) + floor(worldPoint.y) + floor(worldPoint.z)).toInt() % 2 == 0)
            pattern1.colorAt(worldPoint)
        else
            pattern2.colorAt(worldPoint)
}
