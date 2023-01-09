package math

// By Sebastian Raaphorst, 2023.

import output.Show
import kotlin.math.cos
import kotlin.math.sin

data class Matrix(val values: List<List<Double>>, val m: Int = 4, val n: Int = 4): CanBeList<Double>, Show {
    init {
        if (m <= 0 || n <= 0)
            throw IllegalArgumentException("Illegal matrix size: ($m,$n).")
        if (values.size != m)
            throw IllegalArgumentException("Matrix was specified to have $m rows, but has ${values.size}.")
        val illegalRows = values.filterNot { it.size == n }
        if (illegalRows.isNotEmpty())
            throw IllegalArgumentException(
                "Matrix was specified to have $n columns, but has illegal rows: " +
                        illegalRows.joinToString(", ")
            )
    }

    fun isTransformation(): Boolean =
        m == 4 && n == 4

    operator fun get(i: Int, j: Int): Double =
        values[i][j]

    fun row(i: Int): List<Double> =
       values[i]

    fun col(j: Int): List<Double> =
        values.map { it[j] }

    override fun toList(): List<Double> =
        values.flatten()

    operator fun plus(other: Matrix): Matrix {
        if (n != other.n || m != other.m)
            throw ArithmeticException("Cannot add a ($m,$n) matrix and a (${other.m},${other.n}) matrix.")
        val newValues = values.zip(other.values).map {
            (l1, l2) -> l1.zip(l2).map {
                (e1, e2) -> e1 + e2
            }
        }
        return Matrix(newValues, m, n)
    }

    operator fun times(other: Matrix): Matrix {
        if (n != other.m)
            throw ArithmeticException("Cannot multiply a ($m,$n) matrix and a (${other.m},${other.n} matrix.")
        val newValues = (0 until m).map { i ->
            (0 until other.n).map { j ->
                (0 until n).sumOf { k -> this[i, k] * other[k, j] }
            }
        }
        return Matrix(newValues, m, other.n)
    }

    operator fun times(t: Tuple): Tuple {
        if (n != 4 || m != 4)
            throw ArithmeticException("Cannot multiply a ($n,$m) matrix and a tuple.")
        val (x, y, z, w) = (0 until 4).map {
                r -> this[r,0] * t.x + this[r,1] * t.y + this[r,2] * t.z + this[r,3] * t.w
        }
        return Tuple(x, y, z, w)
    }

    fun andThen(other: Matrix): Matrix =
        other * this

    val transpose: Matrix by lazy {
        return@lazy Matrix((0 until n).map { j ->
            (0 until m).map { i ->
                this[i, j]
            }
        }, n, m)
    }

    fun submatrix(x: Int, y: Int): Matrix =
        Matrix((0 until m-1).map { i -> (0 until n-1).map { j ->
            this[if (i < x) i else i + 1, if (j < y) j else j + 1]
        } }, m-1, n-1)

    private fun minor(x: Int, y: Int): Double =
        submatrix(x, y).determinant

    private fun cofactor(x: Int, y: Int): Double =
        (if ((x + y) % 2 == 1) -1.0 else 1.0) * minor(x, y)

    val determinant: Double by lazy {
        if (m != n)
            throw ArithmeticException("Cannot calculate determinant of non-square matrix:\n${show()}")
        return@lazy when(m) {
            1 -> this[0,0]
            else -> (0 until m).sumOf { this[0,it] * cofactor(0, it ) }
        }
    }

    val inverse: Matrix by lazy {
        if (almostEquals(0, determinant))
            throw ArithmeticException("Matrix has determinant 0 and cannot be inverted:\n${show()}")
        return@lazy Matrix(
            (0 until m).map { x ->
                (0 until n).map { y ->
                    cofactor(y, x) / determinant
                }
            }, m, n)
    }

    override fun show(): String {
        // Get max length of string representation for each column.
        val longest = (0 until n).map { y ->
            (0 until m).maxOf { x-> this[x,y].toString().length }
        }

        // Create string formatted for each column.
        return (0 until m).joinToString("\n") { x ->
            (0 until n).joinToString(", ", "| ", " |",
                transform = { String.format("%${longest[it]}s", this[x, it].toString()) })
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix
        if (m != other.m || n != other.n) return false
        return values.zip(other.values).all { (r1, r2) -> almostEquals(r1, r2) }
     }

    override fun hashCode(): Int =
        31 * (31 * m + n) + values.hashCode()

    companion object {
        fun id(size: Int): Matrix =
            Matrix((0 until size).map { x ->
                (0 until size).map { y ->
                    if (x == y) 1.0 else 0.0
                }
            }, size, size)

        // Identity 4x4 matrix.
        val I = id(4)

        fun zero(size: Int): Matrix =
            Matrix(List(size) { List(size) {0.0} }, size, size)

        val Z = zero(4)

        fun from(values: List<List<Number>>, m: Int = 4, n: Int = 4): Matrix =
                Matrix(values.map { r -> r.map { it.toDouble()} }, m, n)

        fun fromList(values: List<Number>, m: Int, n: Int): Matrix =
                from(values.chunked(n), m, n)

        fun fromVar(m: Int, n: Int, vararg values: Number) =
            from(values.toList().chunked(n), m, n)

        fun translate(x: Number, y: Number, z: Number): Matrix =
            Matrix(listOf(
                listOf(1.0, 0.0, 0.0, x.toDouble()),
                listOf(0.0, 1.0, 0.0, y.toDouble()),
                listOf(0.0, 0.0, 1.0, z.toDouble()),
                listOf(0.0, 0.0, 0.0, 1.0)))

        fun scale(x: Number, y: Number, z: Number): Matrix =
            Matrix(listOf(
                listOf(x.toDouble(), 0.0, 0.0, 0.0),
                listOf(0.0, y.toDouble(), 0.0, 0.0),
                listOf(0.0, 0.0, z.toDouble(), 0.0),
                listOf(0.0, 0.0, 0.0, 1.0)))

        fun rotationX(rad: Number): Matrix =
            Matrix(listOf(
                listOf(1.0, 0.0, 0.0, 0.0),
                listOf(0.0, cos(rad.toDouble()), -sin(rad.toDouble()), 0.0),
                listOf(0.0, sin(rad.toDouble()), cos(rad.toDouble()), 0.0),
                listOf(0.0, 0.0, 0.0, 1.0)))

        fun rotationY(rad: Number): Matrix =
            Matrix(listOf(
                listOf(cos(rad.toDouble()), 0.0, sin(rad.toDouble()), 0.0),
                listOf(0.0, 1.0, 0.0, 0.0),
                listOf(-sin(rad.toDouble()), 0.0, cos(rad.toDouble()), 0.0),
                listOf(0.0, 0.0, 0.0, 1.0)))

        fun rotationZ(rad: Number): Matrix =
            Matrix(listOf(
                listOf(cos(rad.toDouble()), -sin(rad.toDouble()), 0.0, 0.0),
                listOf(sin(rad.toDouble()), cos(rad.toDouble()), 0.0, 0.0),
                listOf(0.0, 0.0, 1.0, 0.0),
                listOf(0.0, 0.0, 0.0, 1.0)))

        fun shear(xy: Number, xz: Number, yx: Number, yz: Number, zx: Number, zy: Number): Matrix =
            Matrix(listOf(
                listOf(1.0, xy.toDouble(), xz.toDouble(), 0.0),
                listOf(yx.toDouble(), 1.0, yz.toDouble(), 0.0),
                listOf(zx.toDouble(), zy.toDouble(), 1.0, 0.0),
                listOf(0.0, 0.0, 0.0, 1.0)))
    }
}

operator fun Double.times(m: Matrix): Matrix =
    Matrix(m.values.map { row -> row.map{ this * it } }, m.m, m.n)

operator fun Number.times(m: Matrix): Matrix =
    this.toDouble() * m
