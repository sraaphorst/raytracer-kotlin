package material

// By Sebastian Raaphorst, 2023.

import light.PointLight
import math.Color
import math.Tuple
import math.assertAlmostEquals
import math.sqrt2by2
import org.junit.jupiter.api.Test
import pattern.SolidPattern
import pattern.StripedPattern
import shapes.Sphere
import kotlin.test.assertEquals

class MaterialTest {
    @Test
    fun `Default material`() {
        val m = Material()
        assertEquals(SolidPattern(Color.WHITE), m.pattern)
        assertAlmostEquals(Material.DEFAULT_AMBIENT, m.ambient)
        assertAlmostEquals(Material.DEFAULT_DIFFUSE, m.diffuse)
        assertAlmostEquals(Material.DEFAULT_SPECULAR, m.specular)
        assertAlmostEquals(Material.DEFAULT_SHININESS, m.shininess)
        assertAlmostEquals(Material.DEFAULT_REFLECTIVITY, m.reflectivity)
        assertAlmostEquals(Material.DEFAULT_TRANSPARENCY, m.transparency)
        assertAlmostEquals(Material.DEFAULT_REFRACTIVE_INDEX, m.refractiveIndex)
    }

    @Test
    fun `Lighting with eye between light and surface`() {
        val eyeV = -Tuple.VZ
        val normalV = -Tuple.VZ
        val light = PointLight(Tuple.point(0, 0, -10))
        val m = Material()
        val result = m.lighting(Sphere(), light, Tuple.PZERO, eyeV, normalV, false)
        assertAlmostEquals(Color(1.9, 1.9, 1.9), result)
    }

    @Test
    fun `Lighting with the eye between light and surface, eye offset 45 deg`() {
        val eyeV = Tuple.vector(0, sqrt2by2, -sqrt2by2)
        val normalV = -Tuple.VZ
        val light = PointLight(Tuple.point(0, 0, -10))
        val m = Material()
        val result = m.lighting(Sphere(), light, Tuple.PZERO, eyeV, normalV, false)
        assertAlmostEquals(Color.WHITE, result)
    }

    @Test
    fun `Lighting with eye opposite surface, light offset 45 deg`() {
        val eyeV = -Tuple.VZ
        val normalV = -Tuple.VZ
        val light = PointLight(Tuple.point(0, 10, -10))
        val m = Material()
        val result = m.lighting(Sphere(), light, Tuple.PZERO, eyeV, normalV, false)
        assertAlmostEquals(Color(0.7364, 0.7364, 0.7364), result)
    }

    @Test
    fun `Lighting with eye in path of reflection vector`() {
        val eyeV = Tuple.vector(0, -sqrt2by2, -sqrt2by2)
        val normalV = -Tuple.VZ
        val light = PointLight(Tuple.point(0, 0, 10))
        val m = Material()
        val result = m.lighting(Sphere(), light, Tuple.PZERO, eyeV, normalV, false)
        assertAlmostEquals(Color(0.1, 0.1, 0.1), result)
    }

    @Test
    fun `Lighting with the surface in shadow`() {
        val eyeV = Tuple.vector(0, 0, -1)
        val normalV = Tuple.vector(0, 0, -1)
        val light = PointLight(Tuple.point(0, 0, -10))
        val m = Material()
        val color = m.lighting(Sphere(), light, Tuple.PZERO, eyeV, normalV, true)
        assertAlmostEquals(Color(0.1, 0.1, 0.1), color)
    }

    @Test
    fun `Lighting with striped pattern applied`() {
        val m = Material(StripedPattern(Color.WHITE, Color.BLACK), 1.0, 0.0, 0.0)
        val eyeV = Tuple.vector(0, 0, -1)
        val normalV = Tuple.vector(0, 0, -1)
        val light = PointLight(Tuple.point(0, 0, -10))
        val s = Sphere()
        val c1 = m.lighting(s, light, Tuple.point(0.9, 0, 0), eyeV, normalV, false)
        val c2 = m.lighting(s, light, Tuple.point(1.1, 0, 0), eyeV, normalV, false)
        assertAlmostEquals(Color.WHITE, c1)
        assertAlmostEquals(Color.BLACK, c2)
    }
}
