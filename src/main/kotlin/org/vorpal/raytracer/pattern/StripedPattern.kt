package org.vorpal.raytracer.pattern

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.math.Color
import org.vorpal.raytracer.math.Matrix
import org.vorpal.raytracer.math.Tuple
import org.vorpal.raytracer.math.posmod
import kotlin.math.floor

class StripedPattern(private val patterns: List<Pattern>, transformation: Matrix = Matrix.I): Pattern(transformation) {
    constructor(color1: Color, color2: Color, transformation: Matrix = Matrix.I):
            this(SolidPattern(color1), SolidPattern(color2), transformation)

    constructor(vararg colors: Color): this(colors.map(::SolidPattern))

    constructor(transformation: Matrix, vararg colors: Color):
            this(colors.map(::SolidPattern), transformation)

    constructor(pattern1: Pattern, pattern2: Pattern, transformation: Matrix = Matrix.I):
            this(listOf(pattern1, pattern2), transformation)

    constructor(vararg patterns: Pattern): this(patterns.toList())

    constructor(transformation: Matrix, vararg patterns: Pattern):
            this(patterns.toList(), transformation)

    override fun patternAt(patternPoint: Tuple): Color {
        val idx = floor(patternPoint.x).toInt() posmod patterns.size
        val p = patterns[idx]
        return p.patternAt(p.transformation.inverse * patternPoint)
    }
}
