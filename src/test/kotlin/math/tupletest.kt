package math

// By Sebastian Raaphorst, 2022.

import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TupleTest {
    @Test
    fun `Tuple with w=1 is a point`() {
        val a = Tuple(4.3, -4.2, 3.1, 1.0)
        assertEquals(4.3, a.x)
        assertEquals(-4.2, a.y)
        assertEquals(3.1, a.z)
        assertEquals(1.0, a.w)
        assertTrue(a.isPoint())
        assertFalse(a.isVector())
    }

    @Test
    fun `Tuple with w=0 is a vector`() {
        val a = Tuple(4.3, -4.2, 3.1, 0.0)
        assertEquals(4.3, a.x)
        assertEquals(-4.2, a.y)
        assertEquals(3.1, a.z)
        assertEquals(0.0, a.w)
        assertFalse(a.isPoint())
        assertTrue(a.isVector())
    }

//    @Test
//    fun `Tuple with w=2 fails`() {
//        assertThrows<AssertionError> { Tuple(1.0, 1.0, 1.0, 2.0) }
//    }

    @Test
    fun `point creates tuples with w=1`() {
        val p = Tuple.point(4, -4, 3)
        assertEquals(Tuple(4, -4, 3, 1), p)
    }

    @Test
    fun `vector creates tuples with w=0`() {
        val v = Tuple.vector(4, -4, 3)
        assertEquals(Tuple(4, -4, 3, 0), v)
    }

    @Test
    fun `Tuple precision equality`() {
        val v1 = Tuple.point(1.000001, 2.000005, 3.000007)
        val v2 = Tuple.point(1.000002, 2.000009, 3.000001)
        assertEquals(v1, v2)
    }

    @Test
    fun `Adding vector to point`() {
        val p = Tuple.point(3, -2, 5)
        val v = Tuple.vector(-2, 3, 1)
        assertEquals(Tuple(1, 1, 6, 1), p + v)
    }

    @Test
    fun `Adding vector to vector`() {
        val v1 = Tuple.vector(3, -2, 5)
        val v2 = Tuple.vector(-2, 3, 1)
        assertEquals(Tuple(1, 1, 6, 0), v1 + v2)
    }

    @Test
    fun `Subtracting two points`() {
        val p1 = Tuple.point(3, 2, 1)
        val p2 = Tuple.point(5, 6, 7)
        assertEquals(Tuple.vector(-2, -4, -6), p1 - p2)
    }

    @Test
    fun `Subtracting a vector from a point`() {
        val p = Tuple.point(3, 2, 1)
        val v = Tuple.vector(5, 6, 7)
        assertEquals(Tuple.point(-2, -4, -6), p - v)
    }

    @Test
    fun `Subtracting two vectors`() {
        val v1 = Tuple.vector(3, 2, 1)
        val v2 = Tuple.vector(5, 6, 7)
        assertEquals(Tuple.vector(-2, -4, -6), v1 - v2)
    }

    @Test
    fun `Subtracting a vector from the zero vector`() {
        val v = Tuple.vector(1, -2, 3)
        assertEquals(Tuple.vector(-1, 2, -3), Tuple.VZERO - v)
    }

    @Test
    fun `Negating a tuple`() {
        val t = Tuple(1, -2, 3, -4)
        assertEquals(Tuple(-1, 2, -3, 4), -t)
    }

    @Test
    fun `Multiplying a tuple by a scalar`() {
        val t = Tuple(1, -2, 3, -4)
        assertEquals(Tuple(3.5, -7, 10.5, -14), t * 3.5)
    }

    @Test
    fun `Multiplying a scalar by a tuple`() {
        val t = Tuple(1, -2, 3, -4)
        assertEquals(Tuple(0.5, -1, 1.5, -2), 0.5 * t)
    }

    @Test
    fun `Dividing a tuple by a scalar`() {
        val t = Tuple(1, -2, 3, -4)
        assertEquals(Tuple(0.5, -1, 1.5, -2), t / 2)
    }

    @Test
    fun `Computing the magnitude of VX`() {
        assertEquals(1.0, Tuple.VX.magnitude)
    }

    @Test
    fun `Computing the magnitude of VY`() {
        assertEquals(1.0, Tuple.VY.magnitude)
    }

    @Test
    fun `Computing the magnitude of VZ`() {
        assertEquals(1.0, Tuple.VZ.magnitude)
    }

    @Test
    fun `Computing the magnitude of vector(1,4,8)`() {
        assertAlmostEquals(9.0, Tuple.vector(1, 4, 8).magnitude)
    }

    @Test
    fun `Computing the magnitude of vector(-1,-4,-8)`() {
        assertAlmostEquals(9.0, Tuple.vector(-1, -4, -8).magnitude)
    }
}
