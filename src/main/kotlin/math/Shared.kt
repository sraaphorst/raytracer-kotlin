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

fun durandKernerSolver2(coefficients: List<Double>,
                        start: Cartesian = Cartesian(0.4, 0.9)): List<Cartesian> {
    // Evaluate the polynomial represented by the coefficients at x.
    fun evaluatePolynomial(x: Cartesian): Cartesian =
        coefficients.withIndex().fold(Cartesian.ZERO) { sum, coefficientInfo ->
            val (n, coefficient) = coefficientInfo
            sum + coefficient * x.ipow(n)
        }

    fun roundComplex(v: Cartesian): Cartesian {
        if (v.isReal) return v
        val i = v.im
        val r = v.re
        return if (i.absoluteValue < 0.00001) Cartesian(r, 0) else v
    }

    val n = coefficients.size - 1
    val roots = (0 until n).map { start.ipow(it) }.toMutableList()

    while (true) {
//        println(roots)

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

//        val maxDiff = diffs.max()
//        println("diffs = $diffs, maxDiff=$maxDiff")
        if (diffs.max() < 0.000001)
            break
    }
    return roots.map(::roundComplex)
}

// The Durand-Kerner method of finding the roots of polynomials.
// We use 0.4 - 0.9i as the default polar since it is neither real nor a root of unity, although
//   any such other value would do. We work with Polars since they are faster and easier.
fun durandKernerSolver(coefficients: List<Double>,
                       polar: Polar = Cartesian(0.4, 0.9).toPolar,
                       precision: Double = DEFAULT_PRECISION): List<Polar> {

    // Evaluate the polynomial represented by coefficients with the specified value for x.
    fun evaluatePolynomial(x: Polar): Polar =
        coefficients.withIndex().fold(Polar.ZERO) { eval, coefficientInfo ->
            val (n, coefficient) = coefficientInfo
            eval + coefficient * x.pow(n)
        }

    // Iterative function of Durand-Kerner, initialized with the default roots to use.
    fun aux(roots: List<Polar> = coefficients.indices.map { polar.pow(it) }): List<Polar> {
        // Find the differences and the new roots.
        val (differences, newRoots) = roots.withIndex().map { (i, rootI) ->
            // Calculate the denominator to be used to calculate the new i-th root,
            // which is the product of all the roots except the i-th root.
            val product = (coefficients.indices - i).fold(Polar(1, 0)) { curr, j ->
                curr * (rootI - roots[j])
            }

            val newRoot = rootI - evaluatePolynomial(rootI) / product
            Pair((newRoot - rootI).magnitude, newRoot)
        }.unzip()

        val maxdiff = differences.max()
        return if (almostEquals(0.0, differences.max(), precision)) newRoots
        else aux(newRoots)
    }

    // Round a polar to get rid of small values.
    fun roundComplex(polar: Polar): Polar = when {
        polar.isReal -> Polar(polar.r, 0)
        polar.isImaginary -> Polar(0, polar.theta)
        else -> polar
    }

    return aux().map(::roundComplex)
}

fun main() {
    val coefficients = listOf(
            1496.5625,
            -909.5575910595585,
            215.01929321300094,
            -23.370672726281093,
            1.0
    )
    println(durandKernerSolver2(coefficients))
}

