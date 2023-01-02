package math

import kotlin.contracts.contract

// By Sebastian Raaphorst, 2023.

data class Matrix(val values: List<List<Double>>, val m: Int = 4, val n: Int = 4): CanBeList<Double> {
//    constructor(values: List<List<Number>>, m: Int = 4, n: Int = 4):
//            this(values.map { r -> r.map { it.toDouble()} }, m, n)

//    constructor(values: List<Number>, m: Int, n: Int):
//            this(values.chunked(m), m, n)

    init {
        assert(values.size == m)
        values.forEach { assert(it.size == n) }
    }

    operator fun get(i: Int, j: Int): Double =
        values[i][j]

    fun row(i: Int): List<Double> =
       values[i]

    fun col(j: Int): List<Double> =
        values.map { it[j] }

    override fun toList(): List<Double> =
        values.flatten()

    operator fun times(other: Matrix): Matrix {
        assert(n == other.m)
        val newValues = (0 until m).map { i ->
            (0 until other.n).map { j ->
                (0 until n).sumOf { k -> this[i, k] * other[k, j] }
            }
        }
        return Matrix(newValues, m, other.n)
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
        // Identity 4x4 matrix.
        val I = from(
            (0 until 4)
                .map { x -> (0 until 4)
                    .map { y -> if (x == y) 1 else 0 } })

        fun from(values: List<List<Number>>, m: Int = 4, n: Int = 4): Matrix =
                Matrix(values.map { r -> r.map { it.toDouble()} }, m, n)

        fun fromList(values: List<Number>, m: Int, n: Int): Matrix =
                from(values.chunked(n), m, n)

        fun fromVar(m: Int, n: Int, vararg values: Number) =
            Matrix.from(values.toList().chunked(n), m, n)
    }
}

fun main() {
    val m1 = Matrix.fromVar(2, 3,
        1, 2, 3,
        4, 5, 6)
    val m2 = Matrix.fromVar(3, 4, -1, 2, -3, 4, -5, 6, -7, 8, -9, 10, -11, 12)
    val m3 = m1 * m2
    println(m1)
    println(m2)
    println(m3)
}