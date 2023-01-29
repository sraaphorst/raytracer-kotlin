package shapes

// By Sebastian Raaphorst, 2023.

import math.Ray
import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

// Most of the logic is shared with PlainTriangleTest, so that is tested there.
class SmoothTriangleTest {
    private val p1 = Tuple.PY
    private val p2 = Tuple.point(-1, 0, 0)
    private val p3 = Tuple.PX
    private val n1 = Tuple.VY
    private val n2 = Tuple.vector(-1, 0, 0)
    private val n3 = Tuple.VX
    private val smoothTriangle = SmoothTriangle(p1, p2, p3, n1, n2, n3)

    @Test
    fun `Intersection can encapsulate u and v`() {
        val x = smoothTriangle.createIntersection(3.5, Pair(0.2, 0.4))
        assertNotNull(x.uv)
        val (u, v) = x.uv
        assertAlmostEquals(0.2, u)
        assertAlmostEquals(0.4, v)
    }

    @Test
    fun `Intersection stores u and v`() {
        val r = Ray(Tuple.point(-0.2, 0.3, -2), Tuple.VZ)
        val xs = smoothTriangle.intersect(r)
        assertTrue(xs.isNotEmpty())
        val x = xs.first()
        assertNotNull(x.uv)
        val (u, v) = x.uv
        assertAlmostEquals(0.45, u)
        assertAlmostEquals(0.25, v)
    }

    @Test
    fun `Interpolates normal`() {
        val x = smoothTriangle.createIntersection(1, Pair(0.45, 0.25))
        val n = smoothTriangle.normalAt(Tuple.PZERO, x)
        assertAlmostEquals(Tuple.vector(-0.5547, 0.83205, 0), n)
    }

    @Test
    fun `Preparing normal`() {
        val x = smoothTriangle.createIntersection(1, Pair(0.45, 0.25))
        val r = Ray(Tuple.point(-0.2, 0.3, -2), Tuple.VZ)
        val xs = listOf(x)
        val comps = x.computations(r, xs)
        assertAlmostEquals(Tuple.vector(-0.5547, 0.83205, 0), comps.normalV)
    }
}
