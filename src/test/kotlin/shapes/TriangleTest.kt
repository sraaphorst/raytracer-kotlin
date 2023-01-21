package shapes

import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TriangleTest {
    @Test
    fun `Constructing a triangle`() {
        val p1 = Tuple.PY
        val p2 = Tuple.point(-1, 0,0)
        val p3 = Tuple.PX
        val t = Triangle(p1, p2, p3)

        assertEquals(p1, t.p1)
        assertEquals(p2, t.p2)
        assertEquals(p3, t.p3)
        assertAlmostEquals(Tuple.vector(-1, -1, 0), t.e1)
        assertAlmostEquals(Tuple.vector(1, -1, 0), t.e2)
        assertAlmostEquals(Tuple.vector(0, 0, -1), t.normal)
    }
}
