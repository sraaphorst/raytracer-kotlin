package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Tuple
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CheckerPatternTest {
    companion object {
        val p = CheckerPattern(Color.WHITE, Color.BLACK)
    }

    @Test
    fun `Checkers repeat in x`() {
        assertEquals(p.colorAt(Tuple.PZERO), Color.WHITE)
        assertEquals(p.colorAt(Tuple.point(0.99, 0, 0)), Color.WHITE)
        assertEquals(p.colorAt(Tuple.point(1.01, 0, 0)), Color.BLACK)
    }

    @Test
    fun `Checkers repeat in y`() {
        assertEquals(p.colorAt(Tuple.PZERO), Color.WHITE)
        assertEquals(p.colorAt(Tuple.point(0, 0.99, 0)), Color.WHITE)
        assertEquals(p.colorAt(Tuple.point(0, 1.01, 0)), Color.BLACK)
    }

    @Test
    fun `Checkers repeat in z`() {
        assertEquals(p.colorAt(Tuple.PZERO), Color.WHITE)
        assertEquals(p.colorAt(Tuple.point(0, 0, 0.99)), Color.WHITE)
        assertEquals(p.colorAt(Tuple.point(0, 0, 1.01)), Color.BLACK)
    }
}
