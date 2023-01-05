package math

// By Sebastian Raaphorst, 2023.

import org.junit.jupiter.api.Test

class RayTest {
    @Test
    fun `Compute point from distance`() {
        val r = Ray(Tuple.point(2, 3, 4), Tuple.VX)
        assertAlmostEquals(Tuple.point(2, 3, 4), r.position(0))
        assertAlmostEquals(Tuple.point(3, 3, 4), r.position(1))
        assertAlmostEquals(Tuple.point(1, 3, 4), r.position(-1))
        assertAlmostEquals(Tuple.point(4.5, 3, 4), r.position(2.5))
    }

    @Test
    fun `Translate ray`() {
        val r = Ray(Tuple.point(1, 2, 3), Tuple.VY)
        val m = Matrix.translate(3, 4, 5)
        val r2 = r.transform(m)
        assertAlmostEquals(Tuple.point(4, 6, 8), r2.origin)
        assertAlmostEquals(Tuple.VY, r2.direction)
    }

    @Test
    fun `Scale ray`() {
        val r = Ray(Tuple.point(1, 2, 3), Tuple.VY)
        val m = Matrix.scale(2, 3, 4)
        val r2 = r.transform(m)
        assertAlmostEquals(Tuple.point(2, 6, 12), r2.origin)
        assertAlmostEquals(Tuple.vector(0, 3, 0), r2.direction)
    }
}
