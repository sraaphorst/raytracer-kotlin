package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple
import math.posmod
import kotlin.math.floor
import kotlin.math.sqrt

class RingPattern(private val patterns: List<Pattern>, transformation: Matrix = Matrix.I): Pattern(transformation) {
    constructor(color1: Color, color2: Color, transformation: Matrix = Matrix.I):
            this(listOf(SolidPattern(color1), SolidPattern(color2)), transformation)

    constructor(pattern1: Pattern, pattern2: Pattern, transformation: Matrix = Matrix.I):
            this(listOf(pattern1, pattern2), transformation)

    constructor(transformation: Matrix = Matrix.I, vararg color: Color):
            this(color.map(::SolidPattern), transformation)

    constructor(transformation: Matrix = Matrix.I, vararg pattern: Pattern):
            this(pattern.toList(), transformation)

    // Color at the pattern point.
    override fun patternAt(patternPoint: Tuple): Color {
        val idx = floor(sqrt(patternPoint.x * patternPoint.x + patternPoint.z * patternPoint.z)).toInt() posmod
                patterns.size
        val p = patterns[idx]
        return p.patternAt(p.transformation.inverse * patternPoint)
    }
}
