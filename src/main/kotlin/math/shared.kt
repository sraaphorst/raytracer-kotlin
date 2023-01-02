package math

// By Sebastian Raaphorst, 2022.

import output.Canvas
import kotlin.math.absoluteValue

const val DEFAULT_PRECISION = 1e-5

interface CanBeList<T> {
    fun toList(): List<T>
}

fun <S : Number, T : Number> almostEquals(x: S,
                                          y: T,
                                          precision: Double = DEFAULT_PRECISION): Boolean =
    (x.toDouble() - y.toDouble()).absoluteValue < precision

fun <S : Number, T : Number> almostEquals(cbl1: CanBeList<S>,
                                          cbl2: CanBeList<T>,
                                          precision: Double = DEFAULT_PRECISION): Boolean {
    if (cbl1 === cbl2) return true

    val l1 = cbl1.toList()
    val l2 = cbl2.toList()
    if (l1.size != l2.size) return false

    return l1.zip(l2).all { (e1, e2) -> almostEquals(e1, e2, precision) }
}

fun <S : Number, T : Number> almostEquals(l1: List<S>,
                                          l2: List<T>,
                                          precision: Double = DEFAULT_PRECISION): Boolean {
    if (l1 === l2) return true
    if (l1.size != l2.size) return false
    return l1.zip(l2).all { (e1, e2) -> almostEquals(e1, e2, precision) }
}

fun <S : Number, T : Number> almostEquals(cbl1: CanBeList<S>,
                                          l2: List<T>,
                                          precision: Double = DEFAULT_PRECISION): Boolean {
    val l1 = cbl1.toList()
    if (l1.size != l2.size) return false
    return l1.zip(l2).all { (e1, e2) -> almostEquals(e1, e2, precision) }
}

fun <S : Number, T : Number> almostEquals(l1: List<S>,
                                          cbl2: CanBeList<T>,
                                          precision: Double = DEFAULT_PRECISION): Boolean {
    val l2 = cbl2.toList()
    if (l1.size != l2.size) return false
    return l1.zip(l2).all { (e1, e2) -> almostEquals(e1, e2, precision) }
}
