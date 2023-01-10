package pattern

import math.Color
import math.Matrix
import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test
import kotlin.math.PI

class BlendedPatternTest {
    companion object {
        // p1 is vertical patter, i.e. at x = 0
        // (B W B W...) with z increasing, so going up.
        private val p1 = StripedPattern(Color.BLUE, Color.WHITE, Matrix.rotationY(PI/2))
        // p2 is horizontal pattern, i.e. at z=0:
        // (W G W G...) with x increasing, so going right.
        private val p2 = StripedPattern(Color.WHITE, Color.GREEN)

        // Blended pattern becomes (first component from p1, second from p2).
        // z increases going up, x increases going right.
        // Bottom left corner is (x=0, z=0)
        // WW  WG  WW  WG
        // BW  BG  BW  BG
        // WW  WG  WW  WG
        // BW  BG  BW  BG

        // BW is (BLUE + WHITE) / 2 = ((0, 0, 1) + (1, 1, 1)) / 2 = (0.5, 0.5, 1.0)
        // WW is (WHITE + WHITE) / 2 = WHITE
        // WG is (WHITE + GREEN) / 2 = (0.5, 1.0, 0.5)
        // BG is (BLUE + GREEN) / 2 = (0.0, 0.5, 0.5)
        val p = BlendedPattern(p1, p2)
    }

    @Test
    fun `BlueWhite patch at (x=0, z=0)`() {
        assertAlmostEquals(Color(0.5, 0.5, 1.0), p.colorAt(Tuple.PZERO))
    }

    @Test
    fun `WhiteWhite patch at (x=0, z=1)`() {
        assertAlmostEquals(Color.WHITE, p.colorAt(Tuple.PZ))
    }

    @Test
    fun `WhiteGreen patch at (x=1, z=1)`() {
        assertAlmostEquals(Color(0.5, 1.0, 0.5), p.colorAt(Tuple.point(1, 0, 1)))
    }

    @Test
    fun `BlueGreen patch at (x=1, z=0)`() {
        assertAlmostEquals(Color(0.0, 0.5, 0.5), p.colorAt(Tuple.PX))
    }
}
