package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple
import kotlin.math.PI

class BlendedPattern(val patterns: List<Pattern>,
                     val blender: (List<Color>) -> Color = AVERAGE,
                     transformation: Matrix = Matrix.I) : Pattern(transformation) {
    constructor(pattern1: Pattern, pattern2: Pattern,
                blender: (List<Color>) -> Color = AVERAGE,
                transformation: Matrix = Matrix.I): this(listOf(pattern1, pattern2), blender, transformation)

    override fun colorAt(worldPoint: Tuple): Color =
        blender(patterns.map { it.colorAt(it.transformation.inverse * worldPoint) } )

    companion object {
        val AVERAGE: (List<Color>) -> Color = { it.reduce { c1, c2 -> c1 + c2 } / it.size }
    }
}
