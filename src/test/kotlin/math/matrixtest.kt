package math

// By Sebastian Raaphorst, 2023.

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.ArithmeticException
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.test.assertEquals

class MatrixTest {
    companion object {
        val sqrt2by2 = sqrt(2.0) / 2.0
    }

    @Test
    fun `Construct 3x4 matrix`() {
        val m1 = Matrix.fromVar(3, 4, 1, 2, 3, 4, 5.5, 6.5, 7.5, 8.5, 9, 10, 11, 12)
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
        val m1 = Matrix.fromVar(3, 4, 1, 2, 3, 4, 5.5, 6.5, 7.5, 8.5, 9, 10, 11, 12)
        val m2 = Matrix.fromList(listOf(1, 2, 3, 4, 5.5, 6.5, 7.5, 8.5, 9, 10, 11, 12), 3, 4)
        assertEquals(m1, m2)
    }

    @Test
    fun `Matrix row calls`() {
        val m1 = Matrix.fromVar(3, 4, 1, 2, 3, 4, 5.5, 6.5, 7.5, 8.5, 9, 10, 11, 12)
        assertAlmostEquals(listOf(1, 2, 3, 4), m1.row(0))
        assertAlmostEquals(listOf(5.5, 6.5, 7.5, 8.5), m1.row(1))
        assertAlmostEquals(listOf(9, 10, 11, 12), m1.row(2))
    }

    @Test
    fun `Matrix col calls`() {
        val m1 = Matrix.fromVar(3, 4, 1, 2, 3, 4, 5.5, 6.5, 7.5, 8.5, 9, 10, 11, 12)
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
        assertEquals(mT, m.transpose)
        assertEquals(m, m.transpose.transpose)
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

    @Test
    fun `Translate moves point`() {
        val t = Matrix.translate(5, -3, 2)
        val p = Tuple.point(-3, 4, 5)
        val expected = Tuple.point(2, 1, 7)
        assertAlmostEquals(expected, t * p)
    }

    @Test
    fun `Translate inverse moves point in negative direction`() {
        val t = Matrix.translate(5, -3, 2)
        val tinv = t.inverse
        val p = Tuple.point(-3, 4, 5)
        val expected = Tuple.point(-8, 7, 3)
        assertAlmostEquals(expected, tinv * p)
    }

    @Test
    fun `Translate does not affect vectors`() {
        val t = Matrix.translate(5, -3, 2)
        val v = Tuple.vector(-3, 4, 5)
        assertAlmostEquals(v, t * v)
    }

    @Test
    fun `Scale scales point`() {
        val t = Matrix.scale(2, 3, 4)
        val p = Tuple.point(-4, 6, 8)
        val expected = Tuple.point(-8, 18, 32)
        assertAlmostEquals(expected, t * p)
    }

    @Test
    fun `Scale scales vector`() {
        val t = Matrix.scale(2, 3, 4)
        val v = Tuple.vector(-4, 6, 8)
        val expected = Tuple.vector(-8, 18, 32)
        assertAlmostEquals(expected, t * v)
    }

    @Test
    fun `Scale inverse shrinks vector`() {
        val t = Matrix.scale(2, 3, 4).inverse
        val v = Tuple.vector(-4, 6, 8)
        val expected = Tuple.vector(-2, 2, 2)
        assertAlmostEquals(expected, t * v)
    }

    @Test
    fun `Scaling with negative value reflects`() {
        val t = Matrix.scale(-1, 1, 1)
        val p = Tuple.point(2, 3, 4)
        val expected = Tuple.point(-2, 3, 4)
        assertAlmostEquals(expected, t * p)
    }

    @Test
    fun `Rotate PY around x axis`() {
        val m1 = Matrix.rotationX(PI / 4.0)
        val expected1 = Tuple.point(0, sqrt2by2, sqrt2by2)
        val m2 = Matrix.rotationX(PI / 2.0)
        val expected2 = Tuple.point(0, 0, 1)
        assertAlmostEquals(expected1, m1 * Tuple.PY)
        assertAlmostEquals(expected2, m2 * Tuple.PY)
    }

    @Test
    fun `Rotate PY inversely around x axis`() {
        val m = Matrix.rotationX(PI / 4.0).inverse
        val expected = Tuple.point(0, sqrt2by2, -sqrt2by2)
        assertAlmostEquals(expected, m * Tuple.PY)
    }

    @Test
    fun `Rotate PZ around y axis`() {
        val m1 = Matrix.rotationY(PI / 4.0)
        val expected1 = Tuple.point(sqrt2by2, 0, sqrt2by2)
        val m2 = Matrix.rotationY(PI / 2.0)
        val expected2 = Tuple.point(1, 0, 0)
        assertAlmostEquals(expected1, m1 * Tuple.PZ)
        assertAlmostEquals(expected2, m2 * Tuple.PZ)
    }

    @Test
    fun `Rotate PZ inversely around y axis`() {
        val m = Matrix.rotationY(PI / 4.0).inverse
        val expected = Tuple.point(-sqrt2by2, 0, sqrt2by2)
        assertAlmostEquals(expected, m * Tuple.PZ)
    }

    @Test
    fun `Rotate PY around z axis`() {
        val m1 = Matrix.rotationZ(PI / 4.0)
        val expected1 = Tuple.point(-sqrt2by2, sqrt2by2, 0)
        val m2 = Matrix.rotationZ(PI / 2.0)
        val expected2 = Tuple.point(-1, 0, 0)
        assertAlmostEquals(expected1, m1 * Tuple.PY)
        assertAlmostEquals(expected2, m2 * Tuple.PY)
    }

    @Test
    fun `Rotate PY inversely around z axis`() {
        val m = Matrix.rotationZ(PI / 4.0).inverse
        val expected = Tuple.point(sqrt2by2, sqrt2by2, 0)
        assertAlmostEquals(expected, m * Tuple.PY)
    }

    @Test
    fun `Shear moves x in proportion to y`() {
        val m = Matrix.shear(1, 0, 0, 0, 0, 0)
        val p = Tuple.point(2, 3, 4)
        val expected = Tuple.point(5, 3, 4)
        assertAlmostEquals(expected, m * p)
    }

    @Test
    fun `Shear moves x in proportion to z`() {
        val m = Matrix.shear(0, 1, 0, 0, 0, 0)
        val p = Tuple.point(2, 3, 4)
        val expected = Tuple.point(6, 3, 4)
        assertAlmostEquals(expected, m * p)
    }

    @Test
    fun `Shear moves y in proportion to x`() {
        val m = Matrix.shear(0, 0, 1, 0, 0, 0)
        val p = Tuple.point(2, 3, 4)
        val expected = Tuple.point(2, 5, 4)
        assertAlmostEquals(expected, m * p)
    }

    @Test
    fun `Shear moves y in proportion to z`() {
        val m = Matrix.shear(0, 0, 0, 1, 0, 0)
        val p = Tuple.point(2, 3, 4)
        val expected = Tuple.point(2, 7, 4)
        assertAlmostEquals(expected, m * p)
    }

    @Test
    fun `Shear moves z in proportion to x`() {
        val m = Matrix.shear(0, 0, 0, 0, 1, 0)
        val p = Tuple.point(2, 3, 4)
        val expected = Tuple.point(2, 3, 6)
        assertAlmostEquals(expected, m * p)
    }

    @Test
    fun `Shear moves z in proportion to y`() {
        val m = Matrix.shear(0, 0, 0, 0, 0, 1)
        val p = Tuple.point(2, 3, 4)
        val expected = Tuple.point(2, 3, 7)
        assertAlmostEquals(expected, m * p)
    }

    @Test
    fun `Chaining translations`() {
        val p = Tuple.point(1, 0, 1)
        val m1 = Matrix.rotationX(PI / 2)
        val m2 = Matrix.scale(5, 5, 5)
        val m3 = Matrix.translate(10, 5, 7)
        assertAlmostEquals(Tuple.point(1, -1, 0), m1 * p)
        assertAlmostEquals(Tuple.point(5, -5, 0), m2 * m1 * p)
        assertAlmostEquals(Tuple.point(15, 0, 7), m3 * m2 * m1 * p)
        assertAlmostEquals(Tuple.point(15, 0, 7), m1.andThen(m2).andThen(m3) * p)
    }

    @Test
    fun `View transformation matrix for the default orientation`() {
        val from = Tuple.PZERO
        val to = Tuple.point(0, 0, -1)
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        assertAlmostEquals(Matrix.I, t)
    }

    @Test
    fun `View transformation matrix looking in +z direction`() {
        val from = Tuple.PZERO
        val to = Tuple.PZ
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        assertAlmostEquals(Matrix.scale(-1, 1, -1), t)
    }

    @Test
    fun `View transformation moves the world`() {
        val from = Tuple.point(0, 0, 8)
        val to = Tuple.PZERO
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        assertAlmostEquals(Matrix.translate(0, 0, -8), t)
    }

    @Test
    fun `View transformation that is arbitrary`() {
        val from = Tuple.point(1, 3, 2)
        val to = Tuple.point(4, -2, 8)
        val up = Tuple.vector(1, 1, 0)
        val t = from.viewTransformationFrom(to, up)
        val expected = Matrix.fromVar(4, 4,
            -0.50709, 0.50709,  0.67612, -2.36643,
             0.76772, 0.60609,  0.12122, -2.82843,
            -0.35857, 0.59761, -0.71714,  0.00000,
             0.00000, 0.00000,  0.00000,  1.00000)
        assertAlmostEquals(expected, t)
    }
}
