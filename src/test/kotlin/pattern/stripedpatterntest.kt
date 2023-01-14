package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Tuple
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StripedPatternTest {
    @Test
    fun `Stripe pattern constant in y`() {
        val pattern = StripedPattern(Color.RED, Color.GREEN, Color.BLUE)
        assertEquals(Color.RED, pattern.patternAt(Tuple.point(0, 0, 0)))
        assertEquals(Color.RED, pattern.patternAt(Tuple.point(0, 1, 0)))
        assertEquals(Color.RED, pattern.patternAt(Tuple.point(0, 2, 0)))
    }

    @Test
    fun `Stripe pattern constant in z`() {
        val pattern = StripedPattern(Color.RED, Color.GREEN, Color.BLUE)
        assertEquals(Color.RED, pattern.patternAt(Tuple.point(0, 0, 1)))
        assertEquals(Color.RED, pattern.patternAt(Tuple.point(0, 0, 2)))
        assertEquals(Color.RED, pattern.patternAt(Tuple.point(0, 0, 3)))
    }

    @Test
    fun `Stripe pattern alternates in z`() {
        val pattern = StripedPattern(Color.RED, Color.GREEN, Color.BLUE)
        assertEquals(Color.RED, pattern.patternAt(Tuple.point(0, 0, 0)))
        assertEquals(Color.GREEN, pattern.patternAt(Tuple.point(1, 0, 0)))
        assertEquals(Color.BLUE, pattern.patternAt(Tuple.point(2, 0, 0)))
        assertEquals(Color.RED, pattern.patternAt(Tuple.point(3, 0, 0)))
        assertEquals(Color.GREEN, pattern.patternAt(Tuple.point(4, 0, 0)))
        assertEquals(Color.BLUE, pattern.patternAt(Tuple.point(5, 0, 0)))
    }
}
