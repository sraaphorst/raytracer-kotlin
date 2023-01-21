package math

// By Sebastian Raaphorst, 2022.

import output.Show

data class Color(val r: Double, val g: Double, val b: Double): CanBeList<Double>, Show {
    constructor(r: Number, g: Number, b: Number):
        this(r.toDouble(), g.toDouble(), b.toDouble())

    override fun toList(): List<Double> =
        listOf(r, g, b)
    
    operator fun plus(other: Color): Color =
        Color(r + other.r, g + other.g, b + other.b)

    operator fun minus(other: Color): Color =
        Color(r - other.r, g - other.g, b - other.b)

    operator fun times(scalar: Double): Color =
        Color(scalar * r , scalar * g, scalar * b)

    operator fun times(other: Color): Color =
        Color(r * other.r, g * other.g, b * other.b)

    operator fun times(scalar: Number): Color =
        this * scalar.toDouble()

    operator fun div(scalar: Double): Color =
        Color(r / scalar, g / scalar, b / scalar)

    operator fun div(scalar: Number): Color =
        this / scalar.toDouble()

    fun toHexString(): String =
        "%06x".format(((0xff * r).toInt() shl 16) or ((0xff * g).toInt() shl 8) or (0xff * b).toInt())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Color
        return r == other.r && g == other.g && b == other.b
    }

    override fun hashCode(): Int =
        31 * (31 * r.hashCode() + g.hashCode()) + b.hashCode()

    companion object {
        val BLACK = Color(0, 0, 0)
        val RED = Color(1, 0, 0)
        val GREEN = Color(0, 1, 0)
        val BLUE = Color(0, 0, 1)
        val YELLOW = Color(1, 1, 0)
        val MAGENTA = Color(1, 0, 1)
        val CYAN = Color(0, 1, 1)
        val WHITE = Color(1, 1, 1)

        fun fromHex(rgb: Int): Color {
            if (rgb < 0 || rgb > 0xffffff)
                throw IllegalArgumentException("Illegal hex value for color: ${Integer.toHexString(rgb)}")
            val r = ((rgb and 0xff0000) shr 16).toDouble() / 0xff
            val g = ((rgb and 0x00ff00) shr 8).toDouble() / 0xff
            val b = ((rgb and 0x0000ff)).toDouble() / 0xff
            return Color(r, g, b)
        }

        fun fromHex(r: Int, g: Int, b: Int): Color {
            if (r < 0 || r > 0xff || g < 0 || g > 0xff || b < 0 || b > 0xff)
                throw IllegalArgumentException("Illegal hex value for color: (" +
                        "r=${Integer.toHexString(r)}, " +
                        "g=${Integer.toHexString(g)}, " +
                        "b=${Integer.toHexString(b)})")
            return Color(r.toDouble() / 0xff, g.toDouble() / 0xff, b.toDouble() / 0xff)
        }
    }
}

operator fun Double.times(color: Color): Color =
    Color(this * color.r, this * color.g, this * color.b)

operator fun Number.times(color: Color): Color =
    this.toDouble() * color
