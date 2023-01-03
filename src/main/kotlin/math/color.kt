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

    override fun show(): String = toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Color
        return almostEquals(this, other)
    }

    override fun hashCode(): Int =
        31 * (31 * r.hashCode() + g.hashCode()) + b.hashCode()

    companion object {
        val BLACK = Color(0, 0, 0)
        val RED = Color(1, 0, 0)
        val GREEN = Color(0, 1, 0)
        val BLUE = Color(0, 0, 1)
        val WHITE = Color(1, 1, 1)
    }
}

operator fun Double.times(color: Color): Color =
    Color(this * color.r, this * color.g, this * color.b)

operator fun Number.times(color: Color): Color =
    this.toDouble() * color
