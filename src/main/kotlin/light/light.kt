package light

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Tuple

interface Light {
    val position: Tuple
    val intensity: Color
}
