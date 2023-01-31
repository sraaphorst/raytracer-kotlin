package math

// By Sebastian Raaphorst, 2023.

import kotlin.math.*

sealed interface ComplexBase {
    val isReal: Boolean
    val isImaginary: Boolean
    val re: Double
    val im: Double
    val magnitude: Double
    val toPolar: Polar
    val toCartesian: Cartesian
}

class Polar(val r: Double, theta: Double): ComplexBase {
    constructor(r: Number, theta: Number): this(r.toDouble(), theta.toDouble())

    val theta = fixAngle(theta)

    override val isReal: Boolean
        get() = almostEquals(0.0, theta)

    override val isImaginary: Boolean
        get() = almostEquals(PI / 2, theta.absoluteValue)

    override val re: Double
        get() = r * cos(theta)

    override val im: Double
        get() = r * sin(theta)

    val conjugate: Polar
        get() = Polar(r, -theta)

    override val magnitude: Double
        get() = r

    override val toPolar: Polar = this

    override val toCartesian: Cartesian
        get() = Cartesian(r * cos(theta), r * sin(theta))

    fun pow(n: Number): Polar =
        Polar(r.pow(n.toDouble()), n.toDouble() * theta)

    operator fun unaryMinus(): Polar =
        Polar(r, theta - PI)

    operator fun plus(polar: Polar): Polar =
        (this.toCartesian + polar.toCartesian).toPolar

    operator fun plus(n: Number): Polar =
        (this.toCartesian + n).toPolar

    operator fun minus(polar: Polar): Polar =
        (this.toCartesian - polar.toCartesian).toPolar

    operator fun minus(n: Number): Polar =
        (this.toCartesian - n).toPolar

    operator fun times(polar: Polar): Polar =
        Polar(r * polar.r, theta + polar.theta)

    operator fun times(n: Number): Polar {
        val nd = n.toDouble()
        return if (nd >= 0) Polar(nd * r, theta)
        else Polar(nd.absoluteValue * r, theta + PI)
    }

    operator fun div(polar: Polar): Polar =
        Polar(r / polar.r, theta - polar.theta)

    operator fun div(n: Number): Polar {
        val nd = n.toDouble()
        return if (nd >= 0) Polar(r / nd, theta)
        else Polar(r / nd.absoluteValue, theta - PI)
    }

    override fun toString(): String =
        "Polar(r=$r, theta=$theta)"

    companion object {
        // Zero does not have a unique representation in polar coordinates.
        // Anything with r = 0 is zero.
        val ZERO = Polar(0, 0)
        val ONE = Polar(1, 0)
        val J = Polar(1, PI / 2)

        tailrec fun fixAngle(angle: Double): Double {
            return if (angle >= 0 && angle < 2 * PI) angle
            else if (angle < 0) fixAngle(angle + 2 * PI)
            else fixAngle(angle - 2 * PI)
        }
    }
}

val Number.J: Polar
    get() = Polar(toDouble(), PI / 2)

operator fun Number.plus(polar: Polar): Polar =
    polar + this

operator fun Number.minus(polar: Polar): Polar =
    (toDouble() - polar.toCartesian).toPolar

operator fun Number.times(polar: Polar): Polar =
    polar * this

operator fun Number.div(polar: Polar): Polar {
    val nd = toDouble()
    return if (nd >= 0) Polar(nd / polar.r, -polar.theta)
    else Polar(nd.absoluteValue / polar.r, -polar.theta + PI)
}


data class Cartesian(override val re: Double, override val im: Double): ComplexBase {
    constructor(re: Number, im: Number): this(re.toDouble(), im.toDouble())

    override val isReal: Boolean
        get() = almostEquals(0.0, im)

    override val isImaginary: Boolean
        get() = almostEquals(0.0, re)

    val conjugate: Cartesian
        get() = Cartesian(re, -im)

    override val magnitude: Double
        get() = sqrt(re * re + im * im)

    override val toPolar: Polar
        get() {
            return Polar(magnitude, atan2(im, re))
        }

    override val toCartesian: Cartesian = this

    operator fun unaryMinus(): Cartesian =
        Cartesian(-re, -im)

    operator fun plus(cartesian: Cartesian): Cartesian =
        Cartesian(re + cartesian.re, im + cartesian.im)

    operator fun plus(n: Number): Cartesian =
        Cartesian(re + n.toDouble(), im)

    operator fun minus(cartesian: Cartesian): Cartesian =
        Cartesian(re - cartesian.re, im - cartesian.im)

    operator fun minus(n: Number): Cartesian =
        Cartesian(re - n.toDouble(), im)

    operator fun times(cartesian: Cartesian): Cartesian =
        Cartesian(re * cartesian.re - im * cartesian.im, re * cartesian.im + im * cartesian.re)

    operator fun times(n: Number): Cartesian =
        Cartesian(re * n.toDouble(), im * n.toDouble())

    operator fun div(cartesian: Cartesian): Cartesian =
        Cartesian(
            (re * cartesian.re + im * cartesian.im) / (cartesian.re * cartesian.re + cartesian.im * cartesian.im),
            (im * cartesian.re - re * cartesian.im) / (cartesian.re * cartesian.re + cartesian.im * cartesian.im)
        )

    operator fun div(n: Number): Cartesian =
        Cartesian(re / n.toDouble(), im / n.toDouble())

    // To do power, easier to change into polar coordinates and then change back.
    fun ppow(n: Number): Cartesian =
        toPolar.pow(n).toCartesian

    // If the user insists, can do via Complex, but only to non-negative Int values.
    fun ipow(n: Int): Cartesian =
        if (n < 0) throw ArithmeticException("Cannot evaluate $this.ipow($n): use ppow($n) instead.")
        else if (n == 0) Cartesian(1, 0)
        else this * ipow(n - 1)

    companion object {
        val ZERO = Cartesian(0, 0)
        val ONE = Cartesian(1, 0)
        val I = Cartesian(0, 1)
    }
}

val Number.I: Cartesian
    get() = Cartesian(0.0, toDouble())

operator fun Number.plus(cartesian: Cartesian): Cartesian =
    cartesian + this

operator fun Number.minus(cartesian: Cartesian): Cartesian =
    Cartesian(toDouble() - cartesian.re, -cartesian.im)

operator fun Number.times(cartesian: Cartesian): Cartesian =
    cartesian * this

operator fun Number.div(cartesian: Cartesian): Cartesian =
    Cartesian(
        toDouble() * cartesian.re / (cartesian.re * cartesian.re + cartesian.im * cartesian.im),
        (- toDouble() * cartesian.im) / (cartesian.re * cartesian.re + cartesian.im * cartesian.im)
    )

fun <S : ComplexBase, T : ComplexBase> almostEquals(
    x: S,
    y: T,
    precision: Double = DEFAULT_PRECISION
): Boolean =
        almostEquals(x.toCartesian.re, y.toCartesian.re, precision) &&
                almostEquals(x.toCartesian.im, y.toCartesian.im, precision)

