package math

import kotlin.math.sqrt

// By Sebastian Raaphorst, 2022.

data class Tuple(val x: Double, val y: Double, val z: Double, val w: Double): CanBeList<Double> {
    constructor(x: Number, y: Number, z: Number, w: Number): this(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())

//    init {
//        assert(w == 0.0 || w == 1.0)
//    }

    val magnitude: Double by lazy {
        sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w)
    }

    override fun toList(): List<Double> =
        listOf(x, y, z, w)

    fun isPoint(): Boolean = w == 1.0
    fun isVector(): Boolean = w == 0.0

    operator fun plus(other: Tuple): Tuple =
        Tuple(this.x + other.x, this.y + other.y, this.z + other.z, this.w + other.w)

    operator fun minus(other: Tuple): Tuple =
        Tuple(this.x - other.x, this.y - other.y, this.z - other.z, this.w - other.w)

    // This can only work on vectors.
    operator fun unaryMinus(): Tuple =
        Tuple(-this.x, -this.y, -this.z, -this.w)

    operator fun times(scalar: Double): Tuple =
        Tuple(scalar * this.x, scalar * this.y, scalar * this.z, scalar * this.w)

    operator fun times(scalar: Number): Tuple =
        this * scalar.toDouble()

    operator fun div(scalar: Double): Tuple =
        Tuple(this.x / scalar, this.y / scalar, this.z / scalar, this.w / scalar)

    operator fun div(scalar: Number): Tuple =
        this / scalar.toDouble()

    override fun equals(other: Any?): Boolean =
        other is Tuple
                && almostEquals(this.x, other.x)
                && almostEquals(this.y, other.y)
                && almostEquals(this.z, other.z)
                && this.w == other.w

    override fun hashCode(): Int =
        31 * (31 * (31 * x.hashCode() + y.hashCode()) + z.hashCode()) + w.hashCode()

    companion object {
        fun point(x: Number, y: Number, z: Number): Tuple =
            Tuple(x, y, z, 1.0)
        fun vector(x: Number, y: Number, z: Number): Tuple =
            Tuple(x, y, z, 0.0)

        val VZERO: Tuple by lazy {
            vector(0, 0, 0)
        }

        val VX: Tuple by lazy {
            vector(1, 0, 0)
        }

        val VY: Tuple by lazy {
            vector(0, 1, 0)
        }

        val VZ: Tuple by lazy {
            vector(0, 0, 1)
        }
    }
}

operator fun Double.times(tuple: Tuple): Tuple =
    Tuple(this * tuple.x, this * tuple.y, this * tuple.z, this * tuple.w)

operator fun Number.times(tuple: Tuple): Tuple =
    this.toDouble() * tuple
