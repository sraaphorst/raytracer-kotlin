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
        private val light1 = PointLight(Tuple.point(-10, 10, -10))
        private val m1 = Material(Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)
        val s1 = Sphere(material = m1)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5))
        val w1 = World(listOf(s1, s2), light1)

        private val light2 = PointLight(Tuple.point(0, 0.25, 0))
        val w2 = World(listOf(s1, s2), light2)
    }

    @Test
    fun `Default world`() {
        assertTrue(s1 in w1)
        assertTrue(s2 in w1)
        assertEquals(w1.lights[0], light1)
    }

    @Test
    fun `Intersect default world with ray`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val xs = w1.intersect(r)
        assertAlmostEquals(listOf(4, 4.5, 5.5, 6), xs.map { it.t })
    }

    @Test
    fun `Shading an intersection`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val s = w1.shapes[0]
        val x = Intersection(4, s)
        val comps = x.computations(r)
        val c = w1.shadeHit(comps)
        assertAlmostEquals(Color(0.38066, 0.47583, 0.2855), c)
    }

    @Test
    fun `Shading an intersection from the inside`() {
        val r = Ray(Tuple.PZERO, Tuple.VZ)
        val s = w2.shapes[1]
        val x = Intersection(0.5, s)
        val comps = x.computations(r)
        val c = w2.shadeHit(comps)
        assertAlmostEquals(Color(0.90498, 0.90498, 0.90498), c)
    }

    @Test
    fun `Color when a ray misses`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VY)
        val c = w1.colorAt(r)
        assertEquals(Color.BLACK, c)
    }

    @Test
    fun `Color when a ray hits`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val c = w1.colorAt(r)
        assertAlmostEquals(Color(0.38066, 0.47583, 0.2855), c)
    }

    @Test
    fun `Color with intersection behind the ray`() {
        val m1 = Material(Color(0.8, 1.0, 0.6), 1.0, 0.7, 0.2)
        val s1 = Sphere(material = m1)
        val m2 = Material(ambient=1.0)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5), m2)
        val w = World(listOf(s1, s2), light1)
        val r = Ray(Tuple.point(0, 0, 0.75), Tuple.vector(0, 0, -1))
        val c = w.colorAt(r)
        assertAlmostEquals(m2.color, c)
    }
}
