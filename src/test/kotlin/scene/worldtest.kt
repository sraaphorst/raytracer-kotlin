package scene

// By Sebastian Raaphorst, 2023.

import light.PointLight
import material.Material
import math.*
import org.junit.jupiter.api.Test
import shapes.Sphere
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorldTest {
    @Test
    fun `Default world`() {
        val w = World.DefaultWorld
        assertEquals(2, w.shapes.size)
        assertTrue(w.shapes.all { it in w })
        assertEquals(1, w.lights.size)
        assertEquals(PointLight(Tuple.point(-10, 10, -10)), w.lights[0])
    }

    @Test
    fun `Intersect default world with ray`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val xs = World.DefaultWorld.intersect(r)
        assertAlmostEquals(listOf(4, 4.5, 5.5, 6), xs.map { it.t })
    }

    @Test
    fun `Shading an intersection`() {
        val w = World.DefaultWorld
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val s = w.shapes[0]
        val x = Intersection(4, s)
        val comps = x.computations(r)
        val c = w.shadeHit(comps)
        assertAlmostEquals(Color(0.38066, 0.47583, 0.2855), c)
    }

    @Test
    fun `Shading an intersection from the inside`() {
        val light = PointLight(Tuple.point(0, 0.25, 0))
        val w = World(World.DefaultWorld.shapes, light)
        val r = Ray(Tuple.PZERO, Tuple.VZ)
        val s = w.shapes[1]
        val x = Intersection(0.5, s)
        val comps = x.computations(r)
        val c = w.shadeHit(comps)
        assertAlmostEquals(Color(0.90498, 0.90498, 0.90498), c)
    }

    @Test
    fun `Color when a ray misses`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VY)
        val c = World.DefaultWorld.colorAt(r)
        assertEquals(Color.BLACK, c)
    }

    @Test
    fun `Color when a ray hits`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val c = World.DefaultWorld.colorAt(r)
        assertAlmostEquals(Color(0.38066, 0.47583, 0.2855), c)
    }

    @Test
    fun `Color with intersection behind the ray`() {
        val light = PointLight(Tuple.point(-10, 10, -10))
        val m1 = Material(Color(0.8, 1.0, 0.6), 1.0, 0.7, 0.2)
        val s1 = Sphere(material = m1)
        val m2 = Material(ambient = 1.0)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5), m2)
        val w = World(listOf(s1, s2), light)
        val r = Ray(Tuple.point(0, 0, 0.75), Tuple.vector(0, 0, -1))
        val c = w.colorAt(r)
        assertAlmostEquals(m2.color, c)
    }

    @Test
    fun `No shadow when nothing is collinear with point and light`() {
        val w = World.DefaultWorld
        val p = Tuple.point(0, 10, 0)
        assertFalse(w.isShadowed(p, w.lights.first()))
    }

    @Test
    fun `Shadow when an object is between point and light`() {
        val w = World.DefaultWorld
        val p = Tuple.point(10, -10, 10)
        assertTrue(w.isShadowed(p, w.lights.first()))
    }

    @Test
    fun `No shadow when object behind light`() {
        val w = World.DefaultWorld
        val p = Tuple.point(-20, 20, -20)
        assertFalse(w.isShadowed(p, w.lights.first()))
    }

    @Test
    fun `No shadow when object is behind the point`() {
        val w = World.DefaultWorld
        val p = Tuple.point(-2, 2, -2)
        assertFalse(w.isShadowed(p, w.lights.first()))
    }
}
