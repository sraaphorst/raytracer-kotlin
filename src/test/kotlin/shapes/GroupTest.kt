package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.test.*

class GroupTest {
    @Test
    fun `New group`() {
        val g = Group()
        assertEquals(Matrix.I, g.transformation)
        assertTrue(g.isEmpty)
    }

    @Test
    fun `New group with test shape`() {
        val s = ShapeTest.TestShape()
        val g = Group(children = listOf(s))
        assertEquals(1, g.size)
        val sp = g[0]

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
        val g = Group(listOf(s1, s2, s3))

        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val xs = g.localIntersect(r).sortedBy { it.t }
        assertEquals(4, xs.size)
        val sp1 = g[0]
        val sp2 = g[1]
        assertSame(sp2, xs[0].shape)
        assertSame(sp2, xs[1].shape)
        assertSame(sp1, xs[2].shape)
        assertSame(sp1, xs[3].shape)
    }

    @Test
    fun `Intersect ray with transformed group`() {
        val s = Sphere(transformation = Matrix.translate(5, 0, 0))
        val g = Group(listOf(s), Matrix.scale(2, 2, 2))

        val r = Ray(Tuple.point(10, 0, -10), Tuple.VZ)
        val xs = g.intersect(r)
        assertEquals(2, xs.size)
    }

    @Test
    fun `Calling withXXX properties on groups`() {
        val g1 = run {
            val s1 = Sphere(transformation =
                Matrix.translate(-1, -1, -1) * Matrix.scale(0.5, 0.5, 0.5)
            )
            val c1 = Cylinder(transformation = Matrix.rotateY(PI / 2) * Matrix.scale(0.33, 0.33, 0.33))
            Group(listOf(s1, c1))
        }

        // g1 is no longer relevant:
        // 1. The children of g1g should have g1g as their parent.
        // 2. The parent of g1g should be g2.
        // 3. The transformations of g1 and g1g should still be the same.
        val g2 = Group(listOf(g1), Matrix.scale(0.1, 0.1, 0.1))
        val g1g = g2.children[0] as Group

        assertSame(g2, g1g.parent)
        g1g.forEach { assertSame(g1g, it.parent) }
        g1.zip(g1g).forEach { (s1, s2) ->
            assertNotSame(s1, s2)
            assertSame(s1.transformation, s2.transformation)
        }

        // 1. The children of g1p should have g1p as their parent.
        // 2. The parent of g1p should be g2p.
        // 3. The transformations of g1 and g1p should still be the same.
        val g2p = g2.withTransformation(Matrix.rotateX(PI)) as Group
        val g1p = g2p.children[0] as Group

        assertSame(g2p, g1p.parent)
        g1p.forEach { assertSame(g1p, it.parent) }
        g1.zip(g1p).forEach { (s1, s2) ->
            assertNotSame(s1, s2)
            assertSame(s1.transformation, s2.transformation)
        }

        // Setting a material on g2p should set the exact same material through g2p's children.
        val m = Material(Color.BLUE, ambient = 0.5, specular = 0.2, shininess = 100.0)
        val g2m = g2p.withMaterial(m) as Group
        val g1m = g2m.children[0] as Group

        assertSame(m, g2m.material)
        assertSame(m, g1m.material)
        g1m.forEach { assertSame(m, it.material) }

        assertNotSame(m, g2.material)
        assertNotSame(m, g2p.material)
        assertNotSame(m, g1g.material)
        assertNotSame(m, g1p.material)

        // Make sure the transformations on the shapes have not changed.
        g1.zip(g1g).forEach { (s1, s2) -> assertSame(s1.transformation, s2.transformation) }
        g1.zip(g1p).forEach { (s1, s2) -> assertSame(s1.transformation, s2.transformation) }
        g1.zip(g1m).forEach { (s1, s2) -> assertSame(s1.transformation, s2.transformation) }

        // Make sure the transformations on the groups have / have not changed.
        assertSame(g1.transformation, g1g.transformation)
        assertSame(g1.transformation, g1p.transformation)
        assertSame(g1.transformation, g1m.transformation)
        assertNotSame(g2.transformation, g2p.transformation)
        assertSame(g2m.transformation, g2p.transformation)
    }

    @Test
    fun `Group has bounding box from children union`() {
        val s = Sphere(Matrix.translate(2, 5, -3) * Matrix.scale(2, 2, 2))
        val c = Cylinder(2, 2,
            transformation = Matrix.translate(-4, -1, 4) * Matrix.scale(0.5, 1, 0.5))

        val g = Group(listOf(s, c))
        assertAlmostEquals(Tuple.point(-4.5, 1, -5), g.bounds.minPoint)
        assertAlmostEquals(Tuple.point(4, 7, 4.5), g.bounds.maxPoint)
    }

    @Test
    fun `Intersecting ray with group ignores children`() {
        val c = ShapeTest.TestShape()
        val g = Group(listOf(c))

        val ray = Ray(Tuple.point(0, 0, -5), Tuple.VY)
        val xs = g.intersect(ray)
        assertNull(c.savedRay)
    }

    @Test
    fun `Intersecting ray with group tests children if bounding box hit`() {
        val c = ShapeTest.TestShape()
        val g = Group(listOf(c))
        val newC = g[0] as ShapeTest.TestShape

        val ray = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val xs = g.intersect((ray))
        assertNotNull(newC.savedRay)
    }
}
