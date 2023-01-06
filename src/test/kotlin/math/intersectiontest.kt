package math

// By Sebastian Raaphorst, 2023.

import org.junit.jupiter.api.Test
import shapes.Sphere
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IntersectionTest {
    companion object {
        val s = Sphere()
    }

    @Test
    fun `Intersection encapsulates t and shape`() {
        val i = Intersection(3.5, s)
        assertEquals(3.5, i.t)
        assertEquals(s, i.shape)
    }

    @Test
    fun `hit when all intersections have positive t`() {
        val i1 = Intersection(1, s)
        val i2 = Intersection(2, s)
        val xs = intersections(i1, i2)
        assertEquals(i1, xs.hit())
    }

    @Test
    fun `hit when some intersections have negative t`() {
        val i1 = Intersection(-1, s)
        val i2 = Intersection(1, s)
        val xs = intersections(i1, i2)
        assertEquals(i2, xs.hit())
    }

    @Test
    fun `hit when all intersections have negative t`() {
        val i1 = Intersection(-2, s)
        val i2 = Intersection(-1, s)
        val xs = intersections(i1, i2)
        assertEquals(null, xs.hit())
    }

    @Test
    fun `hit is always min nonnegative intersection`() {
        val i1 = Intersection(5, s)
        val i2 = Intersection(7, s)
        val i3 = Intersection(-3, s)
        val i4 = Intersection(2, s)
        val xs = intersections(i1, i2, i3, i4)
        assertEquals(i4, xs.hit())
    }

    @Test
    fun `Computing intersection state`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val x = Intersection(4, s)
        val comps = x.computations(r)
        assertAlmostEquals(x.t, comps.t)
        assertAlmostEquals(Tuple.point(0, 0, -1), comps.point)
        assertAlmostEquals(Tuple.vector(0, 0, -1), comps.eyeV)
        assertAlmostEquals(Tuple.vector(0, 0, -1), comps.normalV)
    }

    @Test
    fun `Hit when intersection occurs on inside`() {
        val r = Ray(Tuple.PZERO, Tuple.VZ)
        val x = Intersection(1, s)
        val comps = x.computations(r)
        assertAlmostEquals(Tuple.PZ, comps.point)
        assertAlmostEquals(Tuple.vector(0, 0, -1), comps.eyeV)
        assertTrue(comps.inside)
        assertAlmostEquals(Tuple.vector(0, 0, -1), comps.normalV)
    }
}
