package output

// By Sebastian Raaphorst, 2022.

import math.Color
import math.times
import java.io.File
import kotlin.math.roundToInt

class Canvas(val width: Int,
             val height: Int,
             private val pixels: Array<Array<Color>> = Array(width) { Array(height) { Color.BLACK } }) {

    fun clear(color: Color) =
        (0 until width).forEach { x -> (0 until height).forEach { y -> this[x, y] = color } }

    operator fun get(x: Int, y: Int): Color {
        if (x !in (0 until width) || y !in (0 until height))
            throw IllegalArgumentException("Canvas size: ($width,$height), tried to get point ($x,$y).")
        return pixels[x][y]
    }

    operator fun set(x: Number, y: Number, color: Color) {
        val xInt = x.toInt()
        val yInt = y.toInt()
        if (xInt !in (0 until width) || yInt !in (0 until height))
            throw IllegalArgumentException("Canvas size: ($width,$height), tried to set point ($xInt,$yInt).")
        pixels[x.toInt()][y.toInt()] = color
    }

    private fun formatRow(y: Int): String {
        var rowStr = ""
        var lineStr = ""

        // Convert the entire line into a row of values from 0 to 255.
        val line = (0 until width).flatMap { x -> (255 * pixels[x][y]).toList().map {
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
            .joinToString("\n", transform = ::formatRow)) + '\n'

    fun toPPMFile(file: File) =
        file.writeText(toPPM())
}
