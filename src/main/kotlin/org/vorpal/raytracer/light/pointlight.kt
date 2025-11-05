package org.vorpal.raytracer.light

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.math.Color
import org.vorpal.raytracer.math.Tuple

class PointLight(position: Tuple, intensity: Color = Color.WHITE): Light(position, intensity)
