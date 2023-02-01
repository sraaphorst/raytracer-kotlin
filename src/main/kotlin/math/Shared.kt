package math

// By Sebastian Raaphorst, 2022.

import kotlin.math.absoluteValue
import kotlin.math.PI
import kotlin.math.max

const val DEFAULT_PRECISION = 1e-5

interface CanBeList<T> {
    fun toList(): List<T>
}

// Take the mod of an Int and make sure the result is positive.
infix fun Int.posmod(mod: Int): Int =
    ((this % mod) + mod) % mod

fun <S : Number, T : Number> almostEquals(
    x: S,
    y: T,
    precision: Double = DEFAULT_PRECISION
): Boolean {
    // In case the values are precisely the same, terminate prematurely.
    if (x == y) return true

    val xd = x.toDouble()
    val yd = y.toDouble()
    val diff = (xd - yd).absoluteValue

    // If the numbers are close enough, we have corner cases, and to avoid them,
    // we use the fixed precision.
    // This handles cases where a number is 0 or extremely close to 0.
    // Otherwise, allow a fractional tolerance based on the values.
    return (diff < precision) || diff < max(xd.absoluteValue, yd.absoluteValue) * precision
}

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

fun degreesToRadians(degrees: Number): Double =
    degrees.toDouble() / 180.0 * PI

fun radiansToDegrees(radians: Number): Double =
    180.0 * radians.toDouble() / PI

fun durandKernerSolver(coefficients: List<Double>,
                       start: Cartesian = Cartesian(0.4, 0.9)): List<Cartesian> {
    // Evaluate the polynomial represented by the coefficients at x.
    fun evaluatePolynomial(x: Cartesian): Cartesian =
        coefficients.withIndex().fold(Cartesian.ZERO) { sum, coefficientInfo ->
            val (n, coefficient) = coefficientInfo
            sum + coefficient * x.ipow(n)
        }

    // Round a complex to real or imaginary if it is close enough.
    fun roundComplex(v: Cartesian): Cartesian =
        if (v.isReal) Cartesian(v.re, 0)
        else if (v.isImaginary) Cartesian(0, v.im)
        else v

    val n = coefficients.size - 1
    val roots = (0 until n).map { start.ipow(it) }.toMutableList()

    while (true) {
        val diffs = (0 until n).map { i ->
            val product = (0 until n).fold(Cartesian.ONE) { currentProduct, j ->
                if (i == j) currentProduct
                else currentProduct * (roots[i] - roots[j])
            }

            val oldRoot = roots[i]
            val newRoot = roots[i] - evaluatePolynomial(roots[i]) / product
            roots[i] = newRoot
            (newRoot - oldRoot).magnitude
        }

        if (diffs.max().isNaN())
            throw ArithmeticException("Durand-Kerner failed to solve polynomial with coefficients: $coefficients.")

        if (almostEquals(0.0, diffs.max()))
            break
    }
    return roots.map(::roundComplex)
}
