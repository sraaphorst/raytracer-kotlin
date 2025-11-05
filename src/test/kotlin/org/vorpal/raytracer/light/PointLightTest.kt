package org.vorpal.raytracer.light

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.math.Color
import org.vorpal.raytracer.math.Tuple
import org.vorpal.raytracer.math.assertAlmostEquals
import org.junit.jupiter.api.Test

class PointLightTest {
    @Test
    fun `Point light has position and intensity`() {
        val position = Tuple.PZERO
        val intensity = Color.WHITE
        val light = PointLight(position, intensity)
        assertAlmostEquals(position, light.position)
        assertAlmostEquals(intensity, light.intensity)
    }
}
