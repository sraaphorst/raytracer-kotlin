package pattern

// By Sebastian Raaphorst, 2023.
// Converted to Kotlin from https://github.com/SRombauts/SimplexNoise

import math.Tuple
import kotlin.math.floor

class SimplexNoisePattern(pattern: Pattern,
                          scale: Double = 0.3,
                          pscale: Double = 0.7)
    : NoisePattern(pattern, scale, pscale, SimplexNoisePattern::noise) {

    companion object {
        private const val F = 1.0 / 3.0
        private const val G = 1.0 / 6.0

        private val pMod12 = p.map { it % 12 }

        private val grad3 = listOf(
            Tuple.vector(1.0, 1.0, 0.0),
            Tuple.vector(-1.0, 1.0, 0.0),
            Tuple.vector(1.0, -1.0, 0.0),
            Tuple.vector(-1.0, -1.0, 0.0),
            Tuple.vector(1.0, 0.0, 1.0),
            Tuple.vector(-1.0, 0.0, 1.0),
            Tuple.vector(1.0, 0.0, -1.0),
            Tuple.vector(-1.0, 0.0, -1.0),
            Tuple.vector(0.0, 1.0, 1.0),
            Tuple.vector(0.0, -1.0, 1.0),
            Tuple.vector(0.0, 1.0, -1.0),
            Tuple.vector(0.0, -1.0, -1.0)
        )

        private fun noise(x: Double, y: Double, z: Double): Double {
            // Skew the input space to determine which simplex cell we're in.
            val s = (x + y + z) * F
            val i = floor(x + s).toInt()
            val j = floor(y + s).toInt()
            val k = floor(z + s).toInt()
            val t = (i + j + k) * G

            // Unskew the cell origin back to (x, y, z) space and calculate distances from cell origin.
            val x0 = x - (i - t)
            val y0 = y - (j - t)
            val z0 = z - (k - t)
            val v0 = Tuple.vector(x0, y0, z0)

            // The simplex shape is a slightly irregular tetrahedron.
            // Determine which simplex we are in. Calculate the offsets for the second and third corner of the
            // simplex in (i, j, k) coordinates.
            val (secondCorner, thirdCorner) = run {
                if (x0 >= y0)
                    if (y0 >= z0) Pair(Triple(1, 0, 0), Triple(1, 1, 0))
                    else if (x0 >= z0) Pair(Triple(1, 0, 0), Triple(1, 0, 1))
                    else Pair(Triple(0, 0, 1), Triple(1, 0, 1))
                else
                    if (y0 < z0) Pair(Triple(0, 0, 1), Triple(0, 1, 1))
                    else if (x0 < z0) Pair(Triple(0, 1, 0), Triple(0, 1, 1))
                    else Pair(Triple(0, 1, 0), Triple(1, 1, 0))
            }
            val (i1, j1, k1) = secondCorner
            val (i2, j2, k2) = thirdCorner

            // Offsets for second, third, and fourth corner in (x, y, z) coords.
            val x1 = x0 - i1 + G
            val y1 = y0 - j1 + G
            val z1 = z0 - k1 + G
            val v1 = Tuple.vector(x1, y1, z1)
            val x2 = x0 - i2 + G
            val y2 = y0 - j2 + G
            val z2 = z0 - k2 + G
            val v2 = Tuple.vector(x2, y2, z2)
            val x3 = x0 - 1.0 + 3.0 * G
            val y3 = y0 - 1.0 + 3.0 * G
            val z3 = z0 - 1.0 + 3.0 * G
            val v3 = Tuple.vector(x3, y3, z3)

            // Work out the hashed gradient indices of the four simplex corners.
            val ii = i and 255
            val jj = j and 255
            val kk = k and 255
            val gi0 = pMod12[ii + p[jj + p[kk]]]
            val gi1 = pMod12[ii + i1 + p[jj + j1 + p[kk + k1]]]
            val gi2 = pMod12[ii + i2 + p[jj + j2 + p[kk + k2]]]
            val gi3 = pMod12[ii + 1 + p[jj + 1 + p[kk + 1]]]

            // Calculate the contribution from the four corners.
            fun corner(v: Tuple, g: Int): Double {
                val q = 0.6 - v.dot(v)
                return if (q < 0) 0.0
                else q * q * q * q * grad3[g].dot(v)
            }

            val n0 = corner(v0, gi0)
            val n1 = corner(v1, gi1)
            val n2 = corner(v2, gi2)
            val n3 = corner(v3, gi3)

            // Add the contributions from each corner to get the final noise value.
            // The result is scaled to stay in [-1, 1].
            return 32.0 * (n0 + n1 + n2 + n3)
        }
    }
}
