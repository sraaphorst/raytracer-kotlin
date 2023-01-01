package math

// By Sebastian Raaphorst, 2002.

import org.junit.jupiter.api.Test

class ColorTest {
    companion object {
        val c1 = Color(0.9, 0.6, 0.75)
        val c2 = Color(0.7, 0.1, 0.25)
    }

    @Test
    fun `Add colors`() {
        assertAlmostEquals(Color(1.6, 0.7, 1), c1 + c2)
    }

    @Test
    fun `Subtract colors`() {
        assertAlmostEquals(Color(0.2, 0.5, 0.5), c1 - c2)
    }

    @Test
    fun `Multiply colors`() {
        val c1 = Color(1, 0.2, 0.4)
        val c2 = Color(0.9, 1, 0.1)
        assertAlmostEquals(Color(0.9, 0.2, 0.04), c1 * c2)
    }
}