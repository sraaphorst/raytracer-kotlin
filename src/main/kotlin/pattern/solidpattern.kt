package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple

class SolidPattern(val color: Color): Pattern(Matrix.I) {
    override fun colorAt(worldPoint: Tuple): Color =
        color
}
