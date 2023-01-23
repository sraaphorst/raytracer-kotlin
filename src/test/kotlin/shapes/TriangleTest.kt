package shapes

// By Sebastian Raaphorst, 2023.

import math.Ray
import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TriangleTest {
    private val p1 = Tuple.PY
    private val p2 = Tuple.point(-1, 0, 0)
    private val p3 = Tuple.PX
    private val t = Triangle(p1, p2, p3)

    @Test
    fun `Constructing a triangle`() {
        assertEquals(p1, t.p1)
        assertEquals(p2, t.p2)
        assertEquals(p3, t.p3)
        assertAlmostEquals(Tuple.vector(-1, -1, 0), t.e1)
        assertAlmostEquals(Tuple.vector(1, -1, 0), t.e2)
        assertAlmostEquals(Tuple.vector(0, 0, -1), t.normal)
    }

    @Test
    fun `Normal on a triangle`() {
        assertEquals(t.normal, t.localNormalAt(Tuple.point(0, 0.5, 0)))
        assertEquals(t.normal, t.localNormalAt(Tuple.point(-0.5, 0.75, 0)))
        assertEquals(t.normal, t.localNormalAt(Tuple.point(0.5, 0.25, 0)))
    }

    @Test
    fun `Ray parallel to triangle`() {
        val r = Ray(Tuple.point(0, -1, -2), Tuple.VY)
        val xs = t.localIntersect(r)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Ray misses p1-p3 edge`() {
        val r = Ray(Tuple.point(1, 1, -2), Tuple.VZ)
        val xs = t.localIntersect(r)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Ray misses p1-p2 edge`() {
        val r = Ray(Tuple.point(-1, 1, -2), Tuple.VZ)
        val xs = t.localIntersect(r)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Ray misses p2-p3 edge`() {
        val r = Ray(Tuple.point(0, -1, -2), Tuple.VZ)
        val xs = t.localIntersect(r)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Ray strikes triangle`() {
        val r = Ray(Tuple.point(0, 0.5, -2), Tuple.VZ)
        val xs = t.localIntersect(r)
        assertEquals(1, xs.size)
        assertAlmostEquals(2, xs[0].t)
    }
}
