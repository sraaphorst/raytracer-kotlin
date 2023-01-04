package math

// By Sebastian Raaphorst, 2023.

data class Ray(val origin: Tuple, val direction: Tuple): Transformable<Ray> {
    init {
        if (!origin.isPoint())
            throw IllegalArgumentException("Ray must have a point for origin.")
        if (!direction.isVector())
            throw IllegalArgumentException("Ray must have a vector for direction.")
    }

    fun position(t: Number): Tuple =
        origin + t * direction

    // We transform the ray to world space.
    // This should be done by using the inverse transformation of the Shape.
    override fun transform(m: Matrix): Ray =
        Ray(m * origin, m * direction)
}
