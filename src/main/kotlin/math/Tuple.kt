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

    fun isPoint(): Boolean =
        w == 1.0

    fun isVector(): Boolean =
        w == 0.0

    fun toPoint(): Tuple =
        Tuple(x, y, z, 1.0)

    fun toVector(): Tuple =
        Tuple(x, y, z, 0.0)

    fun dot(other: Tuple): Double =
        x * other.x + y * other.y + z * other.z + w * other.w

    fun cross(other: Tuple): Tuple {
        if (!isVector() || !other.isVector())
            throw IllegalArgumentException("Cross product can only be done for vectors: $this, $other.")
        return vector(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )
    }

    fun reflect(normal: Tuple): Tuple {
        if (!this.isVector() || !normal.isVector())
            throw IllegalArgumentException("Reflecting tuples requires vectors: v=$this, normal=$normal.")
        return this - normal * 2 * this.dot(normal)
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

    // Calculate the view transformation, i.e. transformation that orients the world relative
    // to your eye; however, we actually move the eye (camera) as it makes more sense than moving
    // the entire world.
    // The `this` parameter is the point in the scene FROM where you want the eye to be.
    // The `to` parameter is the point at which you want the eye to look.
    // The vector up indicates which way is up, as would be expected.
    // Note that as this returns a Matrix, it is tested in matrixtest.
    fun viewTransformationFrom(to: Tuple, up: Tuple): Matrix {
        if (!this.isPoint())
            throw IllegalArgumentException("viewTransformationFrom requires eye point as this: $this.")
        if (!to.isPoint())
            throw IllegalArgumentException("viewTransformationFrom requires to parameter to be a point: $to.")
        if (!up.isVector())
            throw IllegalArgumentException("viewTransformationFrom requires up parameter to be a vector: $up")
        val forward = (to - this).normalized
        val left = forward.cross(up.normalized)

        // This allows up vector to only be approximately up.
        val trueUp = left.cross(forward)

        // Calculate the transformation: an orientation transform and a translation to move scene into place.
        return Matrix.fromVar(4, 4,
                 left.x,       left.y,        left.z, 0,
               trueUp.x,     trueUp.y,      trueUp.z, 0,
            -(forward.x), -(forward.y), -(forward.z), 0,
                       0,            0,            0, 1) *
                Matrix.translate(-(this.x), -(this.y), -(this.z))
    }

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

        val PZERO: Tuple by lazy {
            point(0, 0, 0)
        }

        val PX: Tuple by lazy {
            point(1, 0, 0)
        }

        val PY: Tuple by lazy {
            point(0, 1, 0)
        }

        val PZ: Tuple by lazy {
            point(0, 0, 1)
        }
    }
}

operator fun Double.times(tuple: Tuple): Tuple =
    Tuple(this * tuple.x, this * tuple.y, this * tuple.z, this * tuple.w)

operator fun Number.times(tuple: Tuple): Tuple =
    toDouble() * tuple
