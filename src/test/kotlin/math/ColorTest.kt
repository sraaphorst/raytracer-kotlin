package math

// By Sebastian Raaphorst, 2002.

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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

    @Test
    fun `fromHex r, g, b`() {
        assertEquals(Color.WHITE, Color.fromHex(0xff, 0xff, 0xff))
        assertEquals(Color.BLACK, Color.fromHex(0x00, 0x00, 0x00))
        assertAlmostEquals(Color(0.2, 0.4, 0.6), Color.fromHex(0x33, 0x66, 0x99))
    }

    @Test
    fun `fromHex rgb`() {
        assertEquals(Color.WHITE, Color.fromHex(0xffffff))
        assertEquals(Color.BLACK, Color.fromHex(0x000000))
        assertEquals(Color(0.6, 0.2, 0.4), Color.fromHex(0x993366))
    }

    @Test
    fun `toHexString output`() {
        assertEquals("ffffff", Color.WHITE.toHexString())
        assertEquals("000000", Color.BLACK.toHexString())
        assertEquals("993366", Color(0.6, 0.2, 0.4).toHexString())
    }
}
