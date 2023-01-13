package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple
import kotlin.math.floor

class GradientPattern(private val pattern1: Pattern, private val pattern2: Pattern, transformation: Matrix = Matrix.I)
    : Pattern(transformation) {
    constructor(color1: Color, color2: Color, transformation: Matrix = Matrix.I):
            this(SolidPattern(color1), SolidPattern(color2), transformation)
    override fun patternAt(patternPoint: Tuple): Color {
        val color1 = pattern1.patternAt(pattern1.transformation.inverse * patternPoint)
        val color2 = pattern2.patternAt(pattern2.transformation.inverse * patternPoint)
        return color1 + (color2 - color1) * (patternPoint.x - floor(patternPoint.x))
    }
}
