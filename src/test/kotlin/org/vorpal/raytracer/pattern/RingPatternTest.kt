package org.vorpal.raytracer.pattern

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.math.Color
import org.vorpal.raytracer.math.Tuple
import org.vorpal.raytracer.math.assertAlmostEquals
import org.junit.jupiter.api.Test

class RingPatternTest {
    @Test
    fun `A ring should extend in both x and z`() {
        val p = RingPattern(Color.WHITE, Color.BLACK)
        assertAlmostEquals(p.patternAt(Tuple.PZERO), Color.WHITE)
        assertAlmostEquals(p.patternAt(Tuple.PX), Color.BLACK)
        assertAlmostEquals(p.patternAt(Tuple.PZ), Color.BLACK)
        assertAlmostEquals(p.patternAt(Tuple.point(0.708, 0, 0.708)), Color.BLACK)
    }
}
