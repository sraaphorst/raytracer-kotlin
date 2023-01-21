package light

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test

class PointLightTest {
    @Test
    fun `Point light has position and intensity`() {
        val position = Tuple.PZERO
        val intensity = Color.WHITE
        val light = PointLight(position, intensity)
        assertAlmostEquals(position, light.position)
        assertAlmostEquals(intensity, light.intensity)
    }
}
