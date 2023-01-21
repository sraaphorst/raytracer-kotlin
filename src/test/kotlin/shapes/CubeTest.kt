package shapes

// By Sebastian Raaphorst, 2023.

import math.Ray
import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CubeTest {
    @Test
    fun `Ray intersects cube`() {
        val c = Cube()

        val origins = listOf(
            Tuple.point(5, 0.5, 0),
            Tuple.point(-5, 0.5, 0),
            Tuple.point(0.5 ,5, 0),
            Tuple.point(0.5, -5, 0),
            Tuple.point(0.5, 0, 5),
            Tuple.point(0.5, 0, -5),
            Tuple.point(0, 0.5, 0))

        val directions = listOf(
            -Tuple.VX, Tuple.VX,
            -Tuple.VY, Tuple.VY,
            -Tuple.VZ, Tuple.VZ, Tuple.VZ)

        val t0s = listOf(4, 4, 4, 4, 4, 4, -1)
        val t1s = listOf(6, 6, 6, 6, 6, 6, 1)

        origins.zip(directions).withIndex().forEach { (idx, rayInfo) ->
            val (origin, direction) = rayInfo
            val r = Ray(origin, direction)
            val xs = c.localIntersect(r)
            assertEquals(2, xs.size)
            assertEquals(t0s[idx].toDouble(), xs[0].t)
            assertEquals(t1s[idx].toDouble(), xs[1].t)
        }
    }

    @Test
    fun `Ray misses cube`() {
        val c = Cube()

        val origins = listOf(
            Tuple.point(-2, 0, 0),
            Tuple.point(0, -2, 0),
            Tuple.point(0 ,0, -2),
            Tuple.point(2, 0, 2),
            Tuple.point(0, 2, 2),
            Tuple.point(2, 2, 0))

        val directions = listOf(
            Tuple.vector(0.2673, 0.5345, 0.8018),
            Tuple.vector(0.8018, 0.2673, 0.5345),
            Tuple.vector(0.5345, 0.8018, 0.2673),
            -Tuple.VZ, -Tuple.VY, -Tuple.VX)

        origins.zip(directions).forEach { (origin, direction) ->
            val r = Ray(origin, direction)
            val xs = c.localIntersect(r)
            assertEquals(0, xs.size)
        }
    }

    @Test
    fun `Normal on surface of cube`() {
        val c = Cube()
        val values = listOf(
            Pair(Tuple.point(1, 0.5, -0.8), Tuple.VX),
            Pair(Tuple.point(-1, -0.2, 0.9), -Tuple.VX),
            Pair(Tuple.point(-0.4, 1, -0.1), Tuple.VY),
            Pair(Tuple.point(0.3, -1, -0.7), -Tuple.VY),
            Pair(Tuple.point(-0.6, 0.3, 1), Tuple.VZ),
            Pair(Tuple.point(0.4, 0.4, -1), -Tuple.VZ),
            Pair(Tuple.point(1, 1, 1), Tuple.VX),
            Pair(Tuple.point(-1, -1, -1), -Tuple.VX))

        values.forEach { (point, normal) ->
            assertAlmostEquals(normal, c.localNormalAt(point))
        }
    }
}