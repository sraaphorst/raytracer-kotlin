package math

// By Sebastian Raaphorst, 2023.

interface Transformable<T> {
    fun transform(m: Matrix): T
}
