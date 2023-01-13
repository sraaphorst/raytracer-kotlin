package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Tuple
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SolidPatternTest {
    @Test
    fun `SolidPattern constant at all points`() {
        val pattern = SolidPattern(Color.BLUE)
        assertEquals(Color.BLUE, pattern.patternAt(Tuple.point(0, 0, 0)))
        assertEquals(Color.BLUE, pattern.patternAt(Tuple.point(-1, 0, 1)))
        assertEquals(Color.BLUE, pattern.patternAt(Tuple.point(2, 1, -4)))
        assertEquals(Color.BLUE, pattern.patternAt(Tuple.point(11, 3, 99)))
        assertEquals(Color.BLUE, pattern.patternAt(Tuple.point(-12, 12, 0)))
        assertEquals(Color.BLUE, pattern.patternAt(Tuple.point(5, 5, 5)))
    }
}