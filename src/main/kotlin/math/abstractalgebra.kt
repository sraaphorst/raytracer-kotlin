package math

import kotlin.jvm.internal.Ref.DoubleRef

// By Sebastian Raaphorst, 2023.

interface MultiplicativeGroup<T> {
    val multiplicativeIdentity: T
    fun multiply(a: T, b: T): T
    fun multiplicativeInverse(a: T): T
}

interface AdditiveGroup<T> {
    val additiveIdentity: T
    fun add(a: T, b: T): T
    fun additiveInverse(a: T): T
}

object TupleGroup: AdditiveGroup<Tuple> {
    override val additiveIdentity = Tuple(0, 0, 0, 0)
    override fun add(a: Tuple, b: Tuple): Tuple =
        Tuple(a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w)
    override fun additiveInverse(a: Tuple): Tuple =
        Tuple(-a.x, -a.y, -a.z, -a.w)
}

interface Ring<T>: AdditiveGroup<T>, MultiplicativeGroup<T>

object DoubleRing: Ring<Double> {
    override val multiplicativeIdentity: Double = 1.0
    override fun multiply(a: Double, b: Double): Double = a * b
    override fun multiplicativeInverse(a: Double): Double {
        assert(a != additiveIdentity)
        1.0 / a
    }
    override val additiveIdentity: Double = 0.0

    override fun add(a: Double, b: Double): Double = a + b
    override fun additiveInverse(a: Double): Double = -a
}

interface AdditiveModule<S, R : Ring<S>, T, G : AdditiveGroup<T>> {
    fun scalarMultiplication(r: S, g: T): T
}

object TupleModule: AdditiveModule<Double, DoubleRing, Tuple, TupleGroup> {
    override fun scalarMultiplication(r: Double, g: Tuple): Tuple =
        Tuple(r * g.x, r * g.y, r * g.z, r * g.w)
}

private fun generateIdentityMatrix(side: Int): Matrix =
    Matrix((0 until side).map { x -> (0 until side).map { y ->
        if (x == y) DoubleRing.multiplicativeIdentity else DoubleRing.additiveIdentity
    }}, side, side)

private fun generateZeroMatrix(side: Int): Matrix =
    Matrix(List(side) { List(side) { DoubleRing.additiveIdentity } }, side, side)

object Matrix4x4Ring: Ring<Matrix> {
    override val multiplicativeIdentity = generateIdentityMatrix(4)
    override fun multiply(a: Matrix, b: Matrix): Matrix {
        assert(a.m == 4 && a.n == 4 && b.m == 4 && b.n == 4)

    }

    override fun multiplicativeInverse(a: Matrix): Matrix {
        TODO("Not yet implemented")
    }

    override fun add(a: Matrix, b: Matrix): Matrix {
        TODO("Not yet implemented")
    }

    override fun additiveInverse(a: Matrix): Matrix {
        TODO("Not yet implemented")
    }

    override val additiveIdentity = generateZeroMatrix(4)

}