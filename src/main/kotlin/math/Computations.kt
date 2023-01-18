package math

// By Sebastian Raaphorst, 2023.

import shapes.Shape
import kotlin.math.pow
import kotlin.math.sqrt

internal data class Computations(val t: Double,
                        val shape: Shape,
                        val point: Tuple,
                        val eyeV: Tuple,
                        val normalV: Tuple,
                        val reflectV: Tuple,
                        val inside: Boolean,
                        val n1: Double,
                        val n2: Double) {
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
    val overPoint: Tuple by lazy {
        point + normalV * DEFAULT_PRECISION
    }

    // Adjust point slightly in the direction opposite of normal to bump it below the surface.
    val underPoint: Tuple by lazy {
        point - normalV * DEFAULT_PRECISION
    }

    // Schlick function: calculates a number in interval [0,1].
    // This is the REFLECTANCE and represents what fraction of light is reflected,
    // given surface information at the hit.
    val schlick: Double by lazy {
        // Determine cosine of angle between eye and normal vectors.
        val cos = eyeV.dot(normalV)

        // Total internal reflection can only occur if n1 > n2.
        val cos2 = if (n1 > n2) {
            val n = n1 / n2
            val sin2T = n * n * (1.0 - cos * cos)
            if (sin2T > 1.0) return@lazy 1.0
            sqrt(1.0 - sin2T)
        } else cos

        val r = (n1 - n2) / (n1 + n2)
        val r0 = r * r
        return@lazy r0 + (1 - r0) * (1 - cos2).pow(5)
    }
}
