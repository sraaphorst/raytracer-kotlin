package light

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Tuple

data class PointLight(override val position: Tuple,
                      override val intensity: Color = Color.WHITE): Light {
    init {
        if (!position.isPoint())
            throw IllegalArgumentException("PointLight position must be a point: $position.")
    }
}
