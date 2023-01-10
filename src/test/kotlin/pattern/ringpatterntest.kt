package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test

class RingPatternTest {
    @Test
    fun `A ring should extend in both x and z`() {
        val p = RingPattern(Color.WHITE, Color.BLACK)
        assertAlmostEquals(p.colorAt(Tuple.PZERO), Color.WHITE)
        assertAlmostEquals(p.colorAt(Tuple.PX), Color.BLACK)
        assertAlmostEquals(p.colorAt(Tuple.PZ), Color.BLACK)
        assertAlmostEquals(p.colorAt(Tuple.point(0.708, 0, 0.708)), Color.BLACK)
    }
}
