package math

// By Sebastian Raaphorst, 2023.

import org.junit.jupiter.api.Test
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
        assertAlmostEquals(-38, m3[0,0])
        assertAlmostEquals(44, m3[0,1])
        assertAlmostEquals(-50, m3[0,2])
        assertAlmostEquals(56, m3[0,3])
        assertAlmostEquals(-83, m3[1,0])
        assertAlmostEquals(98, m3[1,1])
        assertAlmostEquals(-113, m3[1,2])
        assertAlmostEquals(128, m3[1,3])
    }

    @Test
    fun `Identity multiplication retains original`() {
        val m = Matrix.fromVar(4, 4, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
        assertEquals(m, Matrix.I * m)
        assertEquals(m, m * Matrix.I)
    }
}
