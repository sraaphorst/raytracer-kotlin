package output

// By Sebastian Raaphorst, 2022.

import math.Color
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestCanvas {
    @Test
    fun `Create a Canvas`() {
        val c = Canvas(10, 20)
        assertEquals(10, c.width)
        assertEquals(20, c.height)
        (0 until 10)
            .forEach { x -> (0 until 20)
                .forEach { y -> assertEquals(Color.BLACK, c.pixel(x, y)) }
            }
    }

    @Test
    fun `Write pixel to Canvas`() {
        val c = Canvas(10, 20)
        c.writePixel(2, 3, Color.RED)
        (0 until 10)
            .forEach { x -> (0 until 20)
                .forEach { y ->
                    assertEquals(if (x == 2 && y == 3) Color.RED else Color.BLACK, c.pixel(x, y)) }}
    }

    @Test
    fun `Canvas to PPM`() {
        val c = Canvas(5, 3)
        c.writePixel(0, 0, Color(1.5, 0, 0))
        c.writePixel(2, 1, Color(0, 0.5, 0))
        c.writePixel(4, 2, Color(-0.5, 0, 1))
        val expected = """
            P3
            5 3
            255
            255 0 0 0 0 0 0 0 0 0 0 0 0 0 0
            0 0 0 0 0 0 0 128 0 0 0 0 0 0 0
            0 0 0 0 0 0 0 0 0 0 0 0 0 0 255
            
        """.trimIndent()
        assertEquals(expected, c.toPPM())
    }

    @Test
    fun `Canvas to PPM with long lines`() {
        val c = Canvas(10, 2)
        c.clear(Color(1, 0.8, 0.6))
        val expected = """
            P3
            5 3
            255
            255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204
            153 255 204 153 255 204 153 255 204 153 255 204 153
            255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204
            153 255 204 153 255 204 153 255 204 153 255 204 153
            
        """.trimIndent()
    }
}