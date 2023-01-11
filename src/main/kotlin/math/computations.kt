package math

// By Sebastian Raaphorst, 2023.

import shapes.Shape

data class Computations(val t: Double,
                        val shape: Shape,
                        val point: Tuple,
                        val eyeV: Tuple,
                        val normalV: Tuple,
                        val reflectV: Tuple,
                        val inside: Boolean) {
    init {
        if (!point.isPoint())
            throw IllegalArgumentException("Computations point is not point: $point,")
        if (!eyeV.isVector())
            throw IllegalArgumentException("Computations eyeV is not vector: $eyeV.")
        if (!normalV.isVector())
            throw IllegalArgumentException("Computations normalV is not vector: $normalV.")
        if (!reflectV.isVector())
            throw IllegalArgumentException("Computations reflectV is not vector: $reflectV.")
    }

    // Adjust point slightly in the direction of normal before testing for shadows.
    // This bumps it above the surface and prevents self-shadowing / acne effect.
    val overPoint = point + normalV * DEFAULT_PRECISION
}
