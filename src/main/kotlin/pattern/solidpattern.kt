package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple

class SolidPattern(val color: Color): Pattern() {
    override fun patternAt(patternPoint: Tuple): Color =
        color

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SolidPattern) return false

        if (color != other.color) return false

        return true
    }

    override fun hashCode(): Int =
        color.hashCode()
}
