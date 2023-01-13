package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test

class GradientPatternTest {
    @Test
    fun `Gradient linearly interpolates on x between colors`() {
        val p = GradientPattern(Color.WHITE, Color.BLACK)
        assertAlmostEquals(p.patternAt(Tuple.point(0, 0, 0)), Color.WHITE)
        assertAlmostEquals(p.patternAt(Tuple.point(0.25, 0, 0)), Color(0.75, 0.75, 0.75))
        assertAlmostEquals(p.patternAt(Tuple.point(0.5, 0, 0)), Color(0.5, 0.5, 0.5))
        assertAlmostEquals(p.patternAt(Tuple.point(0.75, 0, 0)), Color(0.25, 0.25, 0.25))
    }
}
