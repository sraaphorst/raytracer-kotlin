package pattern

// By Sebastian Raaphorst, 2023.
// Converted to Kotlin from https://github.com/SRombauts/SimplexNoise

import math.Color
import math.Tuple
import kotlin.math.floor

class SimplexNoisePattern(val pattern: Pattern): Pattern() {
    override fun patternAt(patternPoint: Tuple): Color {
        val scale = 0.3
        val pscale = 0.7
        val (x, y, z, _) = patternPoint

        val nx = x * pscale + noise(x, y, z + 0) * scale
        val ny = y * pscale + noise(x, y, z + 1) * scale
        val nz = z * pscale + noise(x, y, z + 2) * scale
        val noisyPoint = Tuple.point(nx, ny, nz)
        return pattern.patternAt(pattern.transformation.inverse * noisyPoint)
    }

    companion object {
        private const val F = 1.0 / 3.0
        private const val G = 1.0 / 6.0

        private val permutation = listOf(151, 160, 137, 91, 90, 15,
            131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
            190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
            88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
            77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
            102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
            135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
            5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
            223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
            129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
            251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
            49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
            138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
        )

        private val p = permutation + permutation
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
