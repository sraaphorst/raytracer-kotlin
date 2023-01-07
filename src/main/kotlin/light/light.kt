package light

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Tuple
import math.almostEquals

abstract class Light(val position: Tuple, val intensity: Color) {
    init {
        if (!position.isPoint())
            throw IllegalArgumentException("PointLight position must be a point: $position.")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Light) return false

        if (!almostEquals(position, other.position)) return false
        if (!almostEquals(intensity, other.intensity)) return false

        return true
    }

    override fun hashCode(): Int =
        31 * position.hashCode() + intensity.hashCode()
}
