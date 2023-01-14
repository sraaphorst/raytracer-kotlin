package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple

class SolidPattern(val color: Color): Pattern() {
    override fun patternAt(patternPoint: Tuple): Color =
        color
}
