package shapes

// By Sebastian Raaphorst, 2023.

import math.Matrix
import math.Ray
import math.Tuple
import org.junit.jupiter.api.Test
import kotlin.test.*

class GroupTest {
    @Test
    fun `New group`() {
        val g = Group()
        assertEquals(Matrix.I, g.transformation)
        assertTrue(g.children.isEmpty())
    }

    @Test
    fun `New group with test shape`() {
        val s = ShapeTest.TestShape()
        val g = Group(children = listOf(s))
        assertEquals(1, g.children.size)
        val sp = g.children[0]

        // Check that s has changed and been assigned the group as a parent.
        assertNotSame(s, sp)
        assertEquals(g, sp.parent)

        // Check that g does not contain the initial shape, but does contain the new shape.
        assertFalse(s in g)
        assertTrue(sp in g)
    }

    @Test
    fun `Intersect ray with empty group`() {
        val g = Group()
        val r = Ray(Tuple.PZERO, Tuple.VZ)
        val xs = g.localIntersect(r)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Intersect ray with nonempty group`() {
        val s1 = Sphere()
        val s2 = Sphere(transformation = Matrix.translate(0, 0, -3))
        val s3 = Sphere(transformation = Matrix.translate(5, 0, 0))
        val g = Group(children = listOf(s1, s2, s3))

        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val xs = g.localIntersect(r)
        assertEquals(4, xs.size)
        val sp1 = g.children[0]
        val sp2 = g.children[1]
        assertSame(sp2, xs[0].shape)
        assertSame(sp2, xs[1].shape)
        assertSame(sp1, xs[2].shape)
        assertSame(sp1, xs[3].shape)
    }

    @Test
    fun `Intersect ray with transformed group`() {
        val s = Sphere(transformation = Matrix.translate(5, 0, 0))
        val g = Group(transformation = Matrix.scale(2, 2, 2), children = listOf(s))

        val r = Ray(Tuple.point(10, 0, -10), Tuple.VZ)
        val xs = g.intersect(r)
        assertEquals(2, xs.size)
    }
}
