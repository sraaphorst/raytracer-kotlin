package shapes

// By Sebastian Raaphorst, 2023.

import math.assertAlmostEquals
import math.Intersection
import math.Matrix
import math.Ray
import math.Tuple
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SphereTest {
    @Test
    fun `Two spheres are not the same`() {
        val s1 = Sphere()
        val s2 = Sphere()
        assertNotEquals(s1, s2)
    }

    @Test
    fun `Sphere is equal to itself`() {
        val s = Sphere()
        assertEquals(s, s)
    }

    @Test
    fun `Ray intersects sphere at two points`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val s = Sphere()
        val xs = s.intersect(r)
        val expected = listOf(
            Intersection(4, s),
            Intersection(6, s)
        )
        assertEquals(expected, xs)
    }

    @Test
    fun `Ray intersects sphere at a tangent`() {
        val r = Ray(Tuple.point(0, 1, -5), Tuple.VZ)
        val s = Sphere()
        val xs = s.intersect(r)
        val expected = listOf(
            Intersection(5, s),
            Intersection(5, s)
        )
        assertEquals(expected, xs)
    }

    @Test
    fun `Ray misses sphere`() {
        val r = Ray(Tuple.point(0, 2, -5), Tuple.VZ)
        val s = Sphere()
        val xs = s.intersect(r)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Ray originates inside sphere`() {
        val r = Ray(Tuple.PZERO, Tuple.VZ)
        val s = Sphere()
        val xs = s.intersect(r)
        val expected = listOf(
            Intersection(-1, s),
            Intersection(1, s)
        )
        assertEquals(expected, xs)
    }

    @Test
    fun `Sphere is behind ray`() {
        val r = Ray(Tuple.point(0, 0, 5), Tuple.VZ)
        val s = Sphere()
        val xs = s.intersect(r)
        val expected = listOf(
            Intersection(-6, s),
            Intersection(-4, s)
        )
        assertEquals(expected, xs)
    }

    @Test
    fun `Ray intersects scaled sphere`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val s = Sphere(Matrix.scale(2, 2, 2))
        val xs = s.intersect(r)
        assertAlmostEquals(listOf(3.0, 7.0), xs.map(Intersection::t))
    }

    @Test
    fun `Ray intersects translated sphere`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val s = Sphere(Matrix.translate(5, 0, 0))
        val xs = s.intersect(r)
        assertTrue(xs.isEmpty())
    }
}