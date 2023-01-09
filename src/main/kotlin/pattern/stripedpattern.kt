package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple

class StripedPattern(val colors: List<Color>, transformation: Matrix = Matrix.I): Pattern(transformation) {
    constructor(vararg colors: Color): this(colors.toList())
    constructor(transformation: Matrix, vararg colors: Color): this(colors.toList(), transformation)

    override fun colorAt(worldPoint: Tuple): Color =
        colors[(worldPoint.x.toInt() % colors.size)]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StripedPattern) return false

        if (colors != other.colors) return false

        return true
    }

    override fun hashCode(): Int =
        colors.hashCode()
}
