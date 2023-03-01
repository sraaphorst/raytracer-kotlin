package pattern

// By Sebastian Raaphorst, 2023.

import math.lerp
import kotlin.math.floor

class PerlinNoisePattern(pattern: Pattern, scale: Double = 0.3, pscale: Double = 0.7)
    : NoisePattern(pattern, scale, pscale, PerlinNoisePattern::noise) {

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

        private fun grad(hash: Int, x: Double, y: Double, z: Double): Double {
            // Convert the lower four bits of the hash into 12 gradient directions.
            val h = hash and 15
            val u = if (h < 8) x else y
            val v = if (h < 4) y else if (h == 12 || h == 14) x else z

            val c1 = if ((h and 1) == 0) u else -u
            val c2 = if ((h and 2) == 0) v else -v
            return c1 + c2
        }
    }
}
