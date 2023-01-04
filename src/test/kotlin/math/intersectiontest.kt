package math

// By Sebastian Raaphorst, 2023.

import org.junit.jupiter.api.Test
import shapes.Sphere
import kotlin.test.assertEquals

class IntersectionTest {
    @Test
    fun `Intersection encapsulates t and shape`() {
        val s = Sphere()
        val i = Intersection(3.5, s)
        assertEquals(3.5, i.t)
        assertEquals(s, i.shape)
    }

    @Test
    fun `hit when all intersections have positive t`() {
        val s = Sphere()
        val i1 = Intersection(1, s)
        val i2 = Intersection(2, s)
        val xs = intersections(i1, i2)
        assertEquals(i1, xs.hit())
    }

    @Test
    fun `hit when some intersections have negative t`() {
        val s = Sphere()
        val i1 = Intersection(-1, s)
        val i2 = Intersection(1, s)
        val xs = intersections(i1, i2)
        assertEquals(i2, xs.hit())
    }

    @Test
    fun `hit when all intersections have negative t`() {
        val s = Sphere()
        val i1 = Intersection(-2, s)
        val i2 = Intersection(-1, s)
        val xs = intersections(i1, i2)
        assertEquals(null, xs.hit())
    }

    @Test
    fun `hit is always min nonnegative intersection`() {
        val s = Sphere()
        val i1 = Intersection(5, s)
        val i2 = Intersection(7, s)
        val i3 = Intersection(-3, s)
        val i4 = Intersection(2, s)
        val xs = intersections(i1, i2, i3, i4)
        assertEquals(i4, xs.hit())
    }
}
