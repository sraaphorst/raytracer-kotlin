package math

// By Sebastian Raaphorst, 2023.

import shapes.Shape

data class Intersection(val t: Double, val shape: Shape) {
    constructor(t: Number, shape: Shape):
            this(t.toDouble(), shape)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Intersection) return false

        if (!almostEquals(t, other.t)) return false
        if (shape != other.shape) return false

        return true
    }

    override fun hashCode(): Int {
        var result = t.hashCode()
        result = 31 * result + shape.hashCode()
        return result
    }
}

// Combine variable number of intersections into a list.
fun intersections(vararg xs: Intersection): List<Intersection> =
    xs.toList()

// The hit is the intersection with the lowest non-negative t value.
fun List<Intersection>.hit(): Intersection? =
    this.filter { it.t >= 0 }.minByOrNull { it.t }
