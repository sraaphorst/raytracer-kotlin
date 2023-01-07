package light

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Tuple

class PointLight(position: Tuple, intensity: Color = Color.WHITE): Light(position, intensity)
