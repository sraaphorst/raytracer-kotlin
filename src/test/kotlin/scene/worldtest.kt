package scene

// By Sebastian Raaphorst, 2023.

import light.PointLight
import material.Material
import math.*
import org.junit.jupiter.api.Test
import shapes.Sphere
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorldTest {
    companion object {
        private val light = PointLight(Tuple.point(-10, 10, -10))
        private val m1 = Material(Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)
        val s1 = Sphere(material = m1)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5))
        val w = World(setOf(s1, s2), light)
    }

    @Test
    fun `Default world`() {
        assertTrue(s1 in w)
        assertTrue(s2 in w)
        assertEquals(w.light, light)
    }

    @Test
    fun `Intersect default world with ray`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val xs = w.intersect(r)
        assertAlmostEquals(listOf(4, 4.5, 5.5, 6), xs.map { it.t })
    }
}
