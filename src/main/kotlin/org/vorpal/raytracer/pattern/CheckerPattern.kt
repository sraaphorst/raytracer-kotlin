package org.vorpal.raytracer.pattern

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.math.Color
import org.vorpal.raytracer.math.Matrix
import org.vorpal.raytracer.math.Tuple
import org.vorpal.raytracer.math.posmod
import kotlin.math.floor

class CheckerPattern(private val pattern1: Pattern, private val pattern2: Pattern, transformation: Matrix = Matrix.I)
    : Pattern(transformation) {
    constructor(color1: Color, color2: Color, transformation: Matrix = Matrix.I):
            this(SolidPattern(color1), SolidPattern(color2), transformation)

    override fun patternAt(patternPoint: Tuple): Color =
        if ((floor(patternPoint.x) +
                    floor(patternPoint.y) +
                    floor(patternPoint.z)).toInt() posmod 2 == 0)
            pattern1.patternAt(pattern1.transformation.inverse * patternPoint)
        else
            pattern2.patternAt(pattern2.transformation.inverse * patternPoint)
}
