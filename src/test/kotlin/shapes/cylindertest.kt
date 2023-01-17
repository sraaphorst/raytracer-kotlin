package shapes

// By Sebastian Raaphorst, 2023.

import math.Ray
import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class CylinderTest {
    @Test
    fun `Ray misses cylinder`() {
        val c = Cylinder()

        val values = listOf(
            Pair(Tuple.point(1, 0, 0), Tuple.VY),
            Pair(Tuple.point(0, 0, 0), Tuple.VY),
            Pair(Tuple.point(0, 0, -5), Tuple.vector(1, 1,1)))

        values.forEach { (origin, direction) ->
            val r = Ray(origin, direction.normalized)
            val xs = c.localIntersect(r)
            assertEquals(0, xs.size)
        }
    }

    @Test
    fun `Ray intersects cylinder`() {
        val c = Cylinder()

        val origins = listOf(
            Tuple.point(1, 0, -5),
            Tuple.point(0, 0, -5),
            Tuple.point(0.5, 0, -5))

        val directions = listOf(Tuple.VZ, Tuple.VZ, Tuple.vector(0.1, 1, 1))

        val t0s = listOf(5, 4, 6.80798)
        val t1s = listOf(5, 6, 7.08872)

        origins.zip(directions).withIndex().forEach {(idx, rayInfo) ->
            val (origin, direction) = rayInfo
            val r = Ray(origin, direction.normalized)
            val xs = c.localIntersect(r)
            assertEquals(2, xs.size)
            assertAlmostEquals(t0s[idx].toDouble(), xs[0].t)
            assertAlmostEquals(t1s[idx].toDouble(), xs[1].t)
        }
    }

    @Test
    fun `Normal on surface of cylinder`() {
        val c = Cylinder()

        val values = listOf(
            Pair(Tuple.PX, Tuple.VX),
            Pair(Tuple.point(0, 5, -1), -Tuple.VZ),
            Pair(Tuple.point(0, -2, 1), Tuple.VZ),
            Pair(Tuple.point(-1, 1, 0), -Tuple.VX))

        values.forEach { (point, normal) ->
            assertEquals(normal, c.localNormalAt(point))
        }
    }

    @Test
    fun `Default minimum and maximum of cylinder`() {
        val c = Cylinder()
        assertEquals(Double.NEGATIVE_INFINITY, c.minimum)
        assertEquals(Double.POSITIVE_INFINITY, c.maximum)
    }

    @Test
    fun `Ray intersects constrained cylinder`() {
        val c = Cylinder(1, 2)

        val values = listOf(
            Triple(Tuple.point(0, 1.5, 0), Tuple.vector(0.1, 1, 0), 0),
            Triple(Tuple.point(0, 3, -5), Tuple.VZ, 0),
            Triple(Tuple.point(0, 0, -5), Tuple.VZ, 0),
            Triple(Tuple.point(0, 2, -5), Tuple.VZ, 0),
            Triple(Tuple.point(0, 1, -5), Tuple.VZ, 0),
            Triple(Tuple.point(0, 1.5, -2), Tuple.VZ, 2))

         values.forEach { (point, direction, count) ->
             val r = Ray(point, direction.normalized)
             val xs = c.localIntersect(r)
             assertEquals(count, xs.size)
         }
    }

    @Test
    fun `Default closed value for cylinder`() {
        val c = Cylinder()
        assertFalse(c.closed)
    }

    @Test
    fun `Ray intersects capped cylinder`() {
        val c = Cylinder(1, 2, true)

        val values = listOf(
            Triple(Tuple.point(0, 3, 0), -Tuple.VY, 2),
            Triple(Tuple.point(0, 3, -2), Tuple.vector(0, -1, 2), 2),
            Triple(Tuple.point(0, 4, -2), Tuple.vector(0, -1, 1), 2),
            Triple(Tuple.point(0, 0, -2), Tuple.vector(0, 1, 2), 2),
            Triple(Tuple.point(0, -1, -2), Tuple.vector(0, 1, 1), 2))

        values.forEach { (point, direction, count) ->
            val r = Ray(point, direction.normalized)
            val xs = c.localIntersect(r)
            assertEquals(count, xs.size)
        }
    }

    @Test
    fun `Normal on cylinder end caps`() {
        val c = Cylinder(1, 2, true)

        val values = listOf(
            Pair(Tuple.PY, -Tuple.VY),
            Pair(Tuple.point(0.5, 1, 0), -Tuple.VY),
            Pair(Tuple.point(0, 1, 0.5), -Tuple.VY),
            Pair(Tuple.point(0, 2, 0), Tuple.VY),
            Pair(Tuple.point(0.5, 2, 0), Tuple.VY),
            Pair(Tuple.point(0, 2, 0.5), Tuple.VY))

        values.forEach { (point, normal) ->
            assertEquals(normal, c.localNormalAt(point))
        }
    }
}
