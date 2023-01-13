package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test
import shapes.Sphere

class PatternTest {
    private class TestPattern(transformation: Matrix = Matrix.I): Pattern(transformation) {
        override fun patternAt(patternPoint: Tuple): Color =
            Color(patternPoint.x, patternPoint.y, patternPoint.z)
    }

    @Test
    fun `Pattern with a shape transformation`() {
        val p = TestPattern()
        val s = Sphere(Matrix.scale(2, 2, 2))
        val c = p.colorAtShape(s, Tuple.point(2, 3, 4))
        assertAlmostEquals(Color(1, 1.5, 2), c)
    }

    @Test
    fun `Pattern with pattern transformation`() {
        val p = TestPattern(Matrix.scale(2, 2, 2))
        val s = Sphere()
        val c = p.colorAtShape(s, Tuple.point(2, 3, 4))
        assertAlmostEquals(Color(1, 1.5, 2), c)
    }

    @Test
    fun `Pattern with pattern transformation and shape with shape transformation`() {
        val p = TestPattern(Matrix.translate(0.5, 1, 1.5))
        val s = Sphere(Matrix.scale(2, 2, 2))
        val c = p.colorAtShape(s, Tuple.point(2.5, 3, 3.5))
        assertAlmostEquals(Color(0.75, 0.5, 0.25), c)
    }
}
