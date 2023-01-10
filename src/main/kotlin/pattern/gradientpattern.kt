package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple
import kotlin.math.floor

class GradientPattern(val color1: Color, val color2: Color, transformation: Matrix = Matrix.I)
    : Pattern(transformation) {
    override fun colorAt(worldPoint: Tuple): Color =
        color1 + (color2 - color1) * (worldPoint.x - floor(worldPoint.x))
}