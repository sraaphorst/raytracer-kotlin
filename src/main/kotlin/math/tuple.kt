package math

// By Sebastian Raaphorst, 2022.

import output.Show
import kotlin.math.sqrt

data class Tuple(val x: Double, val y: Double, val z: Double, val w: Double): CanBeList<Double>, Show {
    constructor(x: Number, y: Number, z: Number, w: Number):
            this(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())

    val magnitude: Double by lazy {
        sqrt(x * x + y * y + z * z + w * w)
    }
    
    val normalized: Tuple by lazy {
        Tuple(x / magnitude, y / magnitude, z / magnitude, w / magnitude)
    }

    override fun toList(): List<Double> =
        listOf(x, y, z, w)

    fun isPoint(): Boolean = w == 1.0

    fun isVector(): Boolean = w == 0.0

    fun dot(other: Tuple): Double =
        x * other.x + y * other.y + z * other.z + w * other.w

    fun cross(other: Tuple): Tuple {
        assert(isVector())
        assert(other.isVector())
        return vector(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )
    }

    operator fun plus(other: Tuple): Tuple =
        Tuple(x + other.x, y + other.y, z + other.z, w + other.w)

    operator fun minus(other: Tuple): Tuple =
        Tuple(x - other.x, y - other.y, z - other.z, w - other.w)

    operator fun unaryMinus(): Tuple =
        Tuple(-x, -y, -z, -w)

    operator fun times(scalar: Double): Tuple =
        Tuple(scalar * x, scalar * y, scalar * z, scalar * w)

    operator fun times(scalar: Number): Tuple =
        this * scalar.toDouble()

    operator fun div(scalar: Double): Tuple =
        Tuple(x / scalar, y / scalar, z / scalar, w / scalar)

    operator fun div(scalar: Number): Tuple =
        this / scalar.toDouble()

    override fun show(): String = toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tuple
        return almostEquals(this, other)
    }

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
    toDouble() * tuple
