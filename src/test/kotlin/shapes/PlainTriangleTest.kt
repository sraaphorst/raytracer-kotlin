package shapes

// By Sebastian Raaphorst, 2023.

import math.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlainTriangleTest {
    private val p1 = Tuple.PY
    private val p2 = Tuple.point(-1, 0, 0)
    private val p3 = Tuple.PX
    private val plainTriangle = PlainTriangle(p1, p2, p3)

    @Test
    fun `Constructing a plain triangle`() {
        assertEquals(p1, plainTriangle.p1)
        assertEquals(p2, plainTriangle.p2)
        assertEquals(p3, plainTriangle.p3)
        assertAlmostEquals(Tuple.vector(-1, -1, 0), plainTriangle.e1)
        assertAlmostEquals(Tuple.vector(1, -1, 0), plainTriangle.e2)
        assertAlmostEquals(Tuple.vector(0, 0, -1), plainTriangle.normal)
    }

    @Test
    fun `Normal on a plain triangle`() {
        assertEquals(plainTriangle.normal, plainTriangle.localNormalAt(Tuple.point(0, 0.5, 0)))
        assertEquals(plainTriangle.normal, plainTriangle.localNormalAt(Tuple.point(-0.5, 0.75, 0)))
        assertEquals(plainTriangle.normal, plainTriangle.localNormalAt(Tuple.point(0.5, 0.25, 0)))
    }

    @Test
    fun `Ray parallel to triangle`() {
        val r = Ray(Tuple.point(0, -1, -2), Tuple.VY)
        val xs = plainTriangle.localIntersect(r)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Ray misses p1-p3 edge`() {
        val r = Ray(Tuple.point(1, 1, -2), Tuple.VZ)
        val xs = plainTriangle.localIntersect(r)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Ray misses p1-p2 edge`() {
        val r = Ray(Tuple.point(-1, 1, -2), Tuple.VZ)
        val xs = plainTriangle.localIntersect(r)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Ray misses p2-p3 edge`() {
        val r = Ray(Tuple.point(0, -1, -2), Tuple.VZ)
        val xs = plainTriangle.localIntersect(r)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Ray strikes triangle`() {
        val r = Ray(Tuple.point(0, 0.5, -2), Tuple.VZ)
        val xs = plainTriangle.localIntersect(r)
        assertEquals(1, xs.size)
        assertAlmostEquals(2, xs[0].t)
    }

    @Test
    fun `Triangle has bounding box`() {
        val p1 = Tuple.point(-3, 7, 2)
        val p2 = Tuple.point(6, 2, -4)
        val p3 = Tuple.point(2, -1, -1)
        val t = PlainTriangle(p1, p2, p3)
        assertEquals(Tuple.point(-3, -1, -4), t.bounds.minPoint)
        assertEquals(Tuple.point(6, 7, 2), t.bounds.maxPoint)
    }
}
