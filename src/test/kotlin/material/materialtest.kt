package material

// By Sebastian Raaphorst, 2023.

import light.PointLight
import math.Color
import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test
import kotlin.math.sqrt

class MaterialTest {
    companion object {
        val m = Material()
        val position = Tuple.PZERO
        val sqrt2by2 = sqrt(2.0) / 2
    }
    @Test
    fun `Default material`() {
        assertAlmostEquals(m.color, Color.WHITE)
        assertAlmostEquals(m.ambient, 0.1)
        assertAlmostEquals(m.diffuse, 0.9)
        assertAlmostEquals(m.specular, 0.9)
        assertAlmostEquals(m.shininess, 200.0)
    }

    @Test
    fun `Lighting with eye between light and surface`() {
        val eyeV = -Tuple.VZ
        val normalV = -Tuple.VZ
        val light = PointLight(Tuple.point(0, 0, -10))
        val result = m.lighting(light, position, eyeV, normalV, false)
        assertAlmostEquals(result, Color(1.9, 1.9, 1.9))
    }

    @Test
    fun `Lighting with the eye between light and surface, eye offset 45 deg`() {
        val eyeV = Tuple.vector(0, sqrt2by2, -sqrt2by2)
        val normalV = -Tuple.VZ
        val light = PointLight(Tuple.point(0, 0, -10))
        val result = m.lighting(light, position, eyeV, normalV, false)
        assertAlmostEquals(Color.WHITE, result)
    }

    @Test
    fun `Lighting with eye opposite surface, light offset 45 deg`() {
        val eyeV = -Tuple.VZ
        val normalV = -Tuple.VZ
        val light = PointLight(Tuple.point(0, 10, -10))
        val result = m.lighting(light, position, eyeV, normalV, false)
        assertAlmostEquals(Color(0.7364, 0.7364, 0.7364), result)
    }

    @Test
    fun `Lighting with eye in path of reflection vector`() {
        val eyeV = Tuple.vector(0, -sqrt2by2, -sqrt2by2)
        val normalV = -Tuple.VZ
        val light = PointLight(Tuple.point(0, 0, 10))
        val result = m.lighting(light, position, eyeV, normalV, false)
        assertAlmostEquals(Color(0.1, 0.1, 0.1), result)
    }

    @Test
    fun `Lighting with the surface in shadow`() {
        val eyeV = Tuple.vector(0, 0, -1)
        val normalV = Tuple.vector(0, 0, -1)
        val light = PointLight(Tuple.point(0, 0, -10))
        val color = m.lighting(light, position, eyeV, normalV, true)
        assertAlmostEquals(Color(0.1, 0.1, 0.1), color)
    }
}
