package output

// By Sebastian Raaphorst, 2022.

import math.Color
import math.almostEquals
import math.times
import java.io.File
import kotlin.math.roundToInt

data class Canvas(val width: Int,
                  val height: Int,
                  private val pixels: Array<Array<Color>> = Array(width) { Array(height) { Color.BLACK } }) {

    fun clear(color: Color) =
        (0 until width).forEach { x -> (0 until height).forEach { y -> writePixel(x, y, color) } }

    fun writePixel(x: Int, y: Int, color: Color) {
        assert(x in 0 until width)
        assert(y in 0 until height)
        pixels[x][y] = color
    }

    fun pixel(x: Int, y: Int): Color {
        assert(x in 0 until width)
        assert(y in 0 until height)
        return pixels[x][y]
    }

    private fun format_row(y: Int): String {
        var rowStr = ""
        var lineStr = ""

        // Convert the entire line into a row of values from 0 to 255.
        val line = (0 until width).flatMap { x -> (255 * pixel(x, y)).toList().map {
            it.roundToInt().coerceAtLeast(0).coerceAtMost(255)
        } }
        line.forEach {
            if (lineStr.length + 1 + it.toString().length < 70)
                if (lineStr.isEmpty())
                    lineStr += it
                else
                    lineStr += " $it"
            else {
                rowStr += "$lineStr\n"
                lineStr = it.toString()
            }
        }
        if (lineStr.isNotEmpty())
            rowStr += lineStr
        return rowStr
    }

    fun toPPM(): String =
        ("P3\n$width $height\n255\n" + (0 until height)
            .joinToString("\n", transform = ::format_row)) + '\n'

    fun toPPMFile(file: File) =
        file.writeText(toPPM())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Canvas
        if (width != other.width) return false
        if (height != other.height) return false
//        return pixels.contentDeepEquals(other.pixels))
        return pixels.zip(other.pixels).all { (r1, r2) -> r1.zip(r2).all { (p1, p2) -> almostEquals(p1, p2) } }
    }

    override fun hashCode(): Int =
        31 * (31 * width + height) + pixels.contentDeepHashCode()
}