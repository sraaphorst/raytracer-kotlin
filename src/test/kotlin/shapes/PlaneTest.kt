package shapes

// By Sebastian Raaphorst, 2023.

import math.Ray
import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlaneTest {
    @Test
    fun `Normal of plane is constant everywhere`() {
        val p = Plane()
        assertAlmostEquals(Tuple.VY, p.localNormalAt(Tuple.PZERO))
        assertAlmostEquals(Tuple.VY, p.localNormalAt(Tuple.point(10, 0, -10)))
        assertAlmostEquals(Tuple.VY, p.localNormalAt(Tuple.point(-5, 0, 150)))
    }

    @Test
    fun `Intersect with ray parallel to plane`() {
        val p = Plane()
        val r = Ray(Tuple.point(0, 10, 0), Tuple.vector(0, 0, 1))
        val xs = p.localIntersect(r)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Intersect with coplanar ray`() {
        val p = Plane()
        val r = Ray(Tuple.PZERO, Tuple.VZ)
        val xs = p.localIntersect(r)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Ray intersection from above`() {
        val p = Plane()
        val r = Ray(Tuple.PY, Tuple.vector(0, -1, 0))
        val xs = p.localIntersect(r)
        assertEquals(1, xs.size)
        assertAlmostEquals(1, xs[0].t)
        assertEquals(p, xs[0].shape)
    }

    @Test
    fun `Ray intersection from below`() {
        val p = Plane()
        val r = Ray(Tuple.point(0, -1, 0), Tuple.VY)
        val xs = p.localIntersect(r)
        assertEquals(1, xs.size)
        assertAlmostEquals(1, xs[0].t)
        assertEquals(p, xs[0].shape)
    }

    @Test
    fun `Plane has bounding box`() {
        val p = Plane()
        assertEquals(Tuple.point(Double.NEGATIVE_INFINITY, 0, Double.NEGATIVE_INFINITY), p.bounds.minPoint)
        assertEquals(Tuple.point(Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY), p.bounds.maxPoint)
    }
}
