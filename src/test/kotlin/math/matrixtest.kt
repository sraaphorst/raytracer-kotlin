package math

// By Sebastian Raaphorst, 2023.

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.ArithmeticException
import kotlin.test.assertEquals

class MatrixTest {
    companion object {
        val m1 = Matrix.from(listOf(
            listOf(1, 2, 3, 4),
            listOf(5.5, 6.5, 7.5, 8.5),
            listOf(9, 10, 11, 12),
        ), 3, 4)

        val m2 = Matrix.fromList(listOf(1, 2, 3, 4, 5.5, 6.5, 7.5, 8.5, 9, 10, 11, 12), 3, 4)
    }

    @Test
    fun `Construct 3x4 matrix`() {
        assertEquals(3, m1.m)
        assertEquals(4, m1.n)
        assertAlmostEquals(1, m1[0,0])
        assertAlmostEquals(2, m1[0,1])
        assertAlmostEquals(3, m1[0,2])
        assertAlmostEquals(4, m1[0,3])
        assertAlmostEquals(5.5, m1[1,0])
        assertAlmostEquals(6.5, m1[1,1])
        assertAlmostEquals(7.5, m1[1,2])
        assertAlmostEquals(8.5, m1[1,3])
        assertAlmostEquals(9, m1[2,0])
        assertAlmostEquals(10, m1[2,1])
        assertAlmostEquals(11, m1[2,2])
        assertAlmostEquals(12, m1[2,3])
    }

    @Test
    fun `Construct 3x4 matrix as row`() {
        assertEquals(m1, m2)
    }

    @Test
    fun `Matrix row calls`() {
        assertAlmostEquals(listOf(1, 2, 3, 4), m1.row(0))
        assertAlmostEquals(listOf(5.5, 6.5, 7.5, 8.5), m1.row(1))
        assertAlmostEquals(listOf(9, 10, 11, 12), m1.row(2))
    }

    @Test
    fun `Matrix col calls`() {
        assertAlmostEquals(listOf(1, 5.5, 9), m1.col(0))
        assertAlmostEquals(listOf(2, 6.5, 10), m1.col(1))
        assertAlmostEquals(listOf(3, 7.5, 11), m1.col(2))
        assertAlmostEquals(listOf(4, 8.5, 12), m1.col(3))
    }

    @Test
    fun `Matrix 2x3 and 3x4 multiplication`() {
        val m1 = Matrix.fromVar(2, 3, 1, 2, 3, 4, 5, 6)
        val m2 = Matrix.fromVar(3, 4, -1, 2, -3, 4, -5, 6, -7, 8, -9, 10, -11, 12)
        val m3 = m1 * m2
        assertEquals(2, m3.m)
        assertEquals(4, m3.n)
        assertEquals(-38.0, m3[0,0])
        assertEquals(44.0, m3[0,1])
        assertEquals(-50.0, m3[0,2])
        assertEquals(56.0, m3[0,3])
        assertEquals(-83.0, m3[1,0])
        assertEquals(98.0, m3[1,1])
        assertEquals(-113.0, m3[1,2])
        assertEquals(128.0, m3[1,3])
    }

    @Test
    fun `Create identity matrix`() {
        val m = Matrix.id(3)
        val expected = Matrix.fromVar(3, 3, 1, 0, 0, 0, 1, 0, 0, 0, 1)
        assertEquals(expected, m)
    }

    @Test
    fun `Identity multiplication retains original`() {
        val m = Matrix.fromVar(4, 4, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
        assertEquals(m, Matrix.I * m)
        assertEquals(m, m * Matrix.I)
    }

    @Test
    fun `Create zero matrix`() {
        val m = Matrix.zero(3)
        val expected = Matrix.fromVar(3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        assertEquals(expected, m)
    }

    @Test
    fun `Zero multiplication is zero`() {
        val m = Matrix.fromVar(4, 4, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
        assertEquals(Matrix.Z, Matrix.Z * m)
        assertEquals(Matrix.Z, m * Matrix.Z)
    }

    @Test
    fun `Matrix times tuple`() {
        val m = Matrix.fromVar(4, 4, 1, 2, 3, 4, 2, 4, 4, 2, 8, 6, 4, 1, 0, 0, 0, 1)
        val t = Tuple(1, 2, 3, 1)
        val expected = Tuple(18, 24, 33, 1)
        assertEquals(expected, m * t)
    }

    @Test
    fun `Matrix transpose`() {
        val m = Matrix.fromVar(3, 4, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        val mT = Matrix.fromVar(4, 3, 1, 5, 9, 2, 6, 10, 3, 7, 11, 4, 8, 12)
        assertEquals(mT, m.transpose())
        assertEquals(m, m.transpose().transpose())
    }

    @Test
    fun `Submatrix of 2x2`() {
        val m = Matrix.fromVar(2, 2, 0, 1, 2, 3)
        val expected = Matrix.fromVar(1, 1, 3)
        assertEquals(expected, m.submatrix(0, 0))
    }

    @Test
    fun `Submatrix of 3x3`() {
        val m = Matrix.fromVar(3, 3, 1, 5, 0, -3, 2, 7, 0, 6, -3)
        val expected = Matrix.fromVar(2, 2, -3, 2, 0, 6)
        assertEquals(expected, m.submatrix(0, 2))
    }

    @Test
    fun `Submatrix of 4x4`() {
        val m = Matrix.fromVar(4, 4, -6, 1, 1, 6, -8, 5, 8, 6, -1, 0, 8, 2, -7, 1, -1, 1)
        val expected = Matrix.fromVar(3, 3, -6, 1, 6, -8, 8, 6, -7, -1, 1)
        assertEquals(expected, m.submatrix(2, 1))
    }

    @Test
    fun `Determinant of 1x1 matrix`() {
        val m = Matrix.fromVar(1, 1, 10)
        assertEquals(10.0, m.determinant)
    }

    @Test
    fun `Determinant of 2x2 matrix`() {
        val m = Matrix.fromVar(2, 2, 1, 5, -3, 2)
        assertEquals(17.0, m.determinant)
    }

    @Test
    fun `Determinant of 3x3 matrix`() {
        val m = Matrix.fromVar(3, 3, 1, 2, 6, -5, 8, -4, 2, 6, 4)
        assertEquals(-196.0, m.determinant)
    }

    @Test
    fun `Determinant of 4x4 matrix`() {
        val m = Matrix.fromVar(4, 4, -2, -8, 3, 5, -3, 1, 7, 3, 1, 2, -9, 6, -6, 7, 7, -9)
        assertEquals(-4071.0, m.determinant)
    }

    @Test
    fun `Inverse fail for 4x4 with determinant 0`() {
        val m = Matrix.fromVar(4, 4, -4, 2, -2, -3, 9, 6, 2, 6, 0, -5, 1, -5, 0, 0, 0, 0)
        assertThrows<ArithmeticException> { m.inverse }
    }

    @Test
    fun `Inverse of 4x4 matrix 1`() {
        val m = Matrix.fromVar(4, 4, -5, 2, 6, -8, 1, -5, 1, 8, 7, 7, -6, -7, 1, -3, 7, 4)
        val expected = Matrix.fromVar(4, 4,
            0.21805, 0.45113, 0.24060, -0.04511,
            -0.80827, -1.45677, -0.44361, 0.52068,
            -0.07895, -0.22368, -0.05263, 0.19737,
            -0.52256, -0.81391, -0.30075, 0.30639)
        val mi = m.inverse
        assertAlmostEquals(expected, mi)
        assertAlmostEquals(Matrix.I, mi * m)
        assertAlmostEquals(Matrix.I, m * mi)
    }

    @Test
    fun `Inverse of 4x4 matrix 2`() {
        val m = Matrix.fromVar(4, 4, 8, -5, 9, 2, 7, 5, 6, 1, -6, 0, 9, 6, -3, 0, -9, -4)
        val expected = Matrix.fromVar(4, 4,
            -0.15385, -0.15385, -0.28205, -0.53846,
            -0.07692, 0.12308, 0.02564, 0.03077,
            0.35897, 0.35897, 0.43590, 0.92308,
            -0.69231, -0.69231, -0.76923, -1.92308)
        val mi = m.inverse
        assertAlmostEquals(expected, mi)
        assertAlmostEquals(Matrix.I, mi * m)
        assertAlmostEquals(Matrix.I, m * mi)
    }

    @Test
    fun `Inverse of 4x4 matrix 3`() {
        val m = Matrix.fromVar(4, 4, 9, 3, 0, 9, -5, -2, -6, -3, -4, 9, 6, 4, -7, 6, 6, 2)
        val expected = Matrix.fromVar(4, 4,
            -0.04074, -0.07778, 0.14444, -0.22222,
            -0.07778, 0.03333, 0.36667, -0.33333,
            -0.02901, -0.14630, -0.10926, 0.12963,
            0.17778, 0.06667, -0.26667, 0.33333)
        val mi = m.inverse
        assertAlmostEquals(expected, mi)
        assertAlmostEquals(Matrix.I, mi * m)
        assertAlmostEquals(Matrix.I, m * mi)
    }

    @Test
    fun `Multiply product by inverse`() {
        val m1 = Matrix.fromVar(4, 4, 3, -9, 7, 3, 3, -8, 2, -9, -4, 4, 4, 1, -6, 5, -1, 1)
        val m2 = Matrix.fromVar(4, 4, 8, 2, 2, 2, 3, -1, 7, 0, 7, 0, 5, 4, 6, -2, 0, 5)
        val c = m1 * m2
        assertAlmostEquals(m1, c * m2.inverse)
    }
}
