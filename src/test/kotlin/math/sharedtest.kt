package math

// By Sebastian Raaphorst, 2022.

import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

fun almostEquals(x: Number, y: Number, precision: Double = DEFAULT_PRECISION): Boolean =
    (x.toDouble() - y.toDouble()).absoluteValue < precision

fun assertAlmostEquals(x: Number, y: Number, precision: Double = DEFAULT_PRECISION) =
    assertTrue(almostEquals(x, y, precision))

fun assertNotAlmostEquals(x: Number, y: Number, precision: Double = DEFAULT_PRECISION) =
    assertFalse(almostEquals(x, y, precision))

fun <S : Number, T : Number> assertAlmostEquals(l1: List<S>,
                                                l2: List<T>,
                                                precision: Double = DEFAULT_PRECISION) {
    assertEquals(l1.size, l2.size)
    l1.zip(l2).forEach { (i1, i2) -> assertAlmostEquals(i1, i2, precision) }
}

fun <S : Number, T : Number> assertAlmostEquals(cbl: CanBeList<S>,
                                                l2: List<T>,
                                                precision: Double = DEFAULT_PRECISION) {
    val l1 = cbl.toList()
    assertAlmostEquals(l1, l2, precision)
}

fun <S: Number, T : Number> assertAlmostEquals(l1: List<S>,
                                                cbl: CanBeList<T>,
                                                precision: Double = DEFAULT_PRECISION) {
    val l2 = cbl.toList()
    assertAlmostEquals(l1, l2, precision)
}

fun <S : Number, T: Number> assertAlmostEquals(cbl1: CanBeList<S>,
                                               cbl2: CanBeList<T>,
                                               precision: Double = DEFAULT_PRECISION) {
    val l1 = cbl1.toList()
    val l2 = cbl2.toList()
    assertAlmostEquals(l1, l2, precision)
}

fun <S : Number, T : Number> assertNotAlmostEquals(l1: List<S>,
                                                  l2: List<T>,
                                                  precision: Double = DEFAULT_PRECISION) {
    assertEquals(l1.size, l2.size)
    assertFalse(l1.zip(l2).all { (i1, i2) -> !almostEquals(i1, i2, precision) })
}

fun <S : Number, T : Number> assertNotAlmostEquals(cbl: CanBeList<S>,
                                                   l2: List<T>,
                                                   precision: Double = DEFAULT_PRECISION) {
    val l1 = cbl.toList()
    assertNotAlmostEquals(l1, l2, precision)
}

fun <S : Number, T : Number> assertNotAlmostEquals(l1: List<S>,
                                                   cbl: CanBeList<T>,
                                                   precision: Double = DEFAULT_PRECISION) {
    val l2 = cbl.toList()
    assertNotAlmostEquals(l1, l2, precision)
}

fun <S : Number, T: Number> assertNotAlmostEquals(cbl1: CanBeList<S>,
                                                  cbl2: CanBeList<T>,
                                                  precision: Double = DEFAULT_PRECISION) {
    val l1 = cbl1.toList()
    val l2 = cbl2.toList()
    assertNotAlmostEquals(l1, l2, precision)
}

class SharedTest {
    companion object {
        val ZERO_LIST = listOf(0, 0, 0, 0)
        val ZERO_TUPLE = Tuple(0, 0, 0, 0)
    }

    @Test
    fun `AssertAlmostEquals number`() {
        assertAlmostEquals(0, DEFAULT_PRECISION / 2)
    }

    @Test
    fun `AssertAlmostNotEquals number`() {
        assertNotAlmostEquals(0, DEFAULT_PRECISION * 2)
    }

    @Test
    fun `AssertAlmostEquals list list`() {
        val l2 = listOf(DEFAULT_PRECISION / 2, 0, 0, 0)
        assertAlmostEquals(ZERO_LIST, l2)
    }

    @Test
    fun `AssertAlmostEquals list tuple`() {
        val t = Tuple(0, 0 + DEFAULT_PRECISION / 2, 0, 0)
        assertAlmostEquals(ZERO_LIST, t)
    }

    @Test
    fun `AssertAlmostEquals tuple list`() {
        val t = Tuple(0, 0, 0 + DEFAULT_PRECISION / 2, 0)
        assertAlmostEquals(t, ZERO_LIST)
    }

    @Test
    fun `AssertAlmostEquals tuple tuple`() {
        val t2 = Tuple(0, 0, 0, DEFAULT_PRECISION / 2)
        assertAlmostEquals(ZERO_TUPLE, t2)
    }

    @Test
    fun `AssertNotAlmostEquals list list`() {
        val l = listOf(0, 0, 0, DEFAULT_PRECISION * 2)
        assertNotAlmostEquals(l, ZERO_LIST)
    }

    @Test
    fun `AssertNotAlmostEquals list tuple`() {
        val t = Tuple(DEFAULT_PRECISION * 2, 0, 0, DEFAULT_PRECISION * 2)
        assertNotAlmostEquals(ZERO_LIST, t)
    }

    @Test
    fun `AssertNotAlmostEquals tuple list`() {
        val t = Tuple(0, 0, 0, DEFAULT_PRECISION * 2)
        assertNotAlmostEquals(t, ZERO_LIST)
    }

    @Test
    fun `AssertNotAlmostEquals tuple tuple`() {
        val t2 = Tuple(0, 0, 0, DEFAULT_PRECISION * 2)
        assertNotAlmostEquals(ZERO_TUPLE, t2)
    }
}