package shapes

// By Sebastian Raaphorst, 2023.

import math.Ray
import math.Tuple
import math.assertAlmostEquals
import math.sqrt2
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ConeTest {
    @Test
    fun `Ray intersects cone`() {
        val c = Cone()

        val origins = listOf(
            Tuple.point(0, 0, -5),
            Tuple.point(0, 0, -5),
            Tuple.point(1, 1, -5))

        val directions = listOf(Tuple.VZ, Tuple.vector(1, 1, 1), Tuple.vector(-0.5, -1, 1))

        val t0s = listOf(5, 8.66025, 4.55006)
        val t1s = listOf(5, 8.66025, 49.44994)

        origins.zip(directions).withIndex().forEach { (idx, rayInfo) ->
            val (origin, direction) = rayInfo
            val r = Ray(origin, direction.normalized)
            val xs = c.localIntersect(r)
            assertEquals(2, xs.size)
            assertAlmostEquals(t0s[idx].toDouble(), xs[0].t)
            assertAlmostEquals(t1s[idx].toDouble(), xs[1].t)
        }
    }

    @Test
    fun `Ray is parallel to one of cone halves`() {
        val c = Cone()
        val r = Ray(Tuple.point(0, 0, -1), Tuple.vector(0, 1, 1).normalized)
        val xs = c.localIntersect(r)
        assertEquals(1, xs.size)
        assertAlmostEquals(0.35355, xs[0].t)
    }

    @Test
    fun `Ray intersects capped cone`() {
        val c = Cone(-0.5, 0.5, true)

        val values = listOf(
            Triple(Tuple.point(0, 0, -5), -Tuple.VY, 0),
            Triple(Tuple.point(0, 0, -0.25), Tuple.vector(0, 1, 1), 2),
            Triple(Tuple.point(0, 0, -0.25), Tuple.VY, 4))

        values.forEach { (point, direction, count) ->
            val r = Ray(point, direction.normalized)
            val xs = c.localIntersect(r)
            assertEquals(count, xs.size)
        }
    }

    @Test
    fun `Normal on cone`() {
        val c = Cone()

        val values = listOf(
            Pair(Tuple.PZERO, Tuple.VZERO),
            Pair(Tuple.point(1, 1, 1), Tuple.vector(1, -sqrt2, 1)),
            Pair(Tuple.point(-1, -1, 0), Tuple.vector(-1, 1, 0)))

        values.forEach { (point, normal) ->
            assertEquals(normal, c.localNormalAt(point))
        }
    }
}
