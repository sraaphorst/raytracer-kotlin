package math

// By Sebastian Raaphorst, 2022.

import kotlin.math.absoluteValue

const val DEFAULT_PRECISION = 1e-5

interface CanBeList<T> {
    fun toList(): List<T>
}

fun almostEquals(x: Number, y: Number, precision: Double = DEFAULT_PRECISION): Boolean =
    (x.toDouble() - y.toDouble()).absoluteValue < precision
