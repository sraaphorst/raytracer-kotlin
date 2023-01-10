package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple
import kotlin.math.floor

class GradientPattern(val pattern1: Pattern, val pattern2: Pattern, transformation: Matrix = Matrix.I)
    : Pattern(transformation) {
    constructor(color1: Color, color2: Color, transformation: Matrix = Matrix.I):
            this(SolidPattern(color1), SolidPattern(color2), transformation)
    override fun colorAt(worldPoint: Tuple): Color {
        val color1 = pattern1.colorAt(worldPoint)
        val color2 = pattern2.colorAt(worldPoint)
        return color1 + (color2 - color1) * (worldPoint.x - floor(worldPoint.x))
    }
}