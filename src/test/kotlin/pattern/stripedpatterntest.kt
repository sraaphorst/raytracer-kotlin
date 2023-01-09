package pattern

// By Sebastian Raaphorst, 2023.

import material.Material
import math.Color
import math.Matrix
import math.Tuple
import org.junit.jupiter.api.Test
import shapes.Sphere
import kotlin.test.assertEquals

class StripedPatternTest {
    @Test
    fun `Stripe pattern constant in y`() {
        val pattern = StripedPattern(Color.RED, Color.GREEN, Color.BLUE)
        assertEquals(Color.RED, pattern.colorAt(Tuple.point(0, 0, 0)))
        assertEquals(Color.RED, pattern.colorAt(Tuple.point(0, 1, 0)))
        assertEquals(Color.RED, pattern.colorAt(Tuple.point(0, 2, 0)))
    }

    @Test
    fun `Stripe pattern constant in z`() {
        val pattern = StripedPattern(Color.RED, Color.GREEN, Color.BLUE)
        assertEquals(Color.RED, pattern.colorAt(Tuple.point(0, 0, 1)))
        assertEquals(Color.RED, pattern.colorAt(Tuple.point(0, 0, 2)))
        assertEquals(Color.RED, pattern.colorAt(Tuple.point(0, 0, 3)))
    }

    @Test
    fun `Stripe pattern alternates in z`() {
        val pattern = StripedPattern(Color.RED, Color.GREEN, Color.BLUE)
        assertEquals(Color.RED, pattern.colorAt(Tuple.point(0, 0, 0)))
        assertEquals(Color.GREEN, pattern.colorAt(Tuple.point(1, 0, 0)))
        assertEquals(Color.BLUE, pattern.colorAt(Tuple.point(2, 0, 0)))
        assertEquals(Color.RED, pattern.colorAt(Tuple.point(3, 0, 0)))
        assertEquals(Color.GREEN, pattern.colorAt(Tuple.point(4, 0, 0)))
        assertEquals(Color.BLUE, pattern.colorAt(Tuple.point(5, 0, 0)))
    }
}
