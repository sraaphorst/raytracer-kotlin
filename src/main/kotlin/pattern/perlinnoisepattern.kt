package pattern

import math.Color
import math.Tuple
import kotlin.math.floor

class PerlinNoisePattern(val pattern: Pattern): Pattern() {
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
        fun noise(x: Double, y: Double, z: Double): Double {
            // Find the unit cube that contains the point.
            val ux = floor(x).toInt() and 255
            val uy = floor(y).toInt() and 255
            val uz = floor(z).toInt() and 255

            // Find the relative values of the point in the cube.
            val cx = x - floor(x)
            val cy = y - floor(y)
            val cz = z - floor(z)

            // Compute the fade curves.
            val u = fade(cx)
            val v = fade(cy)
            val w = fade(cz)

            // Hash coordinates of the cube corners.
            val a  = p[ux] + uy
            val aa = p[a] + uz
            val ab = p[a + 1] + uz
            val b  = p[ux + 1] + uy
            val ba = p[b] + uz
            val bb = p[b + 1] + uz

            // Add blended results from the cube corners.
            return lerp(w, lerp(v, lerp(u, grad(p[aa], cx, cy, cz),
                                           grad(p[ba], cx - 1, cy, cz)),
                                   lerp(u, grad(p[ab], cx, cy -1 , cz),
                                           grad(p[bb], cx - 1, cy - 1, cz))),
                           lerp(v, lerp(u, grad(p[aa + 1], cx, cy, cz - 1),
                                           grad(p[ba + 1], cx - 1, cy, cz - 1)),
                                   lerp(u, grad(p[ab + 1], cx, cy - 1, cz - 1),
                                           grad(p[bb + 1], cx - 1, cy - 1, cz - 1))))
        }

        private fun fade(t: Double): Double =
            t * t * t * (t * (t * 6 - 15) + 10)

        private fun lerp(t: Double, a: Double, b: Double): Double =
            a + t * (b - a)

        private fun grad(hash: Int, x: Double, y: Double, z: Double): Double {
            // Convert the lower four bits of the hash into 12 gradient directions.
            val h = hash and 15
            val u = if (h < 8) x else y
            val v = if (h < 4) y else if (h == 12 || h == 14) x else z

            val c1 = if ((h and 1) == 0) u else -u
            val c2 = if ((h and 2) == 0) v else -v
            return c1 + c2
        }

        private val permutation = listOf(151,160,137,91,90,15,
            131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
            190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
            88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
            77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
            102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
            135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
            5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
            223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
            129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
            251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
            49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
            138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
        )
        private val p = permutation + permutation
    }
}