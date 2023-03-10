package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ShapeTest {
    internal class TestShape(transformation: Matrix = Matrix.I,
                    material: Material = Material(),
                    parent: Shape? = null):
        Shape(transformation, material, true, parent) {
        // We need to use a var here to store a ray.
        internal var savedRay: Ray? = null

        override fun withParent(parent: Shape?): Shape {
            val s = TestShape(transformation, material, parent)
            s.savedRay = savedRay
            return s
        }

        override fun withMaterial(material: Material): Shape {
            val s = TestShape(transformation, material, parent)
            s.savedRay = savedRay
            return s
        }

        override fun localIntersect(rayLocal: Ray): List<Intersection> {
            savedRay = rayLocal
            return emptyList()
        }

        override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple =
            localPoint - Tuple.PZERO

        override val bounds: BoundingBox by lazy {
            BoundingBox(
                Tuple.point(-1, -1, -1),
                Tuple.point(1, 1, 1)
            )
        }
    }

    @Test
    fun `Intersecting scaled shape with ray`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val t = Matrix.scale(2, 2, 2)
        val s = TestShape(t)
        s.intersect(r)

        val savedRay = s.savedRay
        assertNotNull(savedRay)
        assertAlmostEquals(Tuple.point(0, 0, -2.5), savedRay.origin)
        assertAlmostEquals(Tuple.vector(0, 0, 0.5), savedRay.direction)
    }

    @Test
    fun `Intersecting translated shape with ray`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val t = Matrix.translate(5, 0, 0)
        val s = TestShape(t)
        s.intersect(r)

        val savedRay = s.savedRay
        assertNotNull(savedRay)
        assertAlmostEquals(Tuple.point(-5, 0, -5), savedRay.origin)
        assertAlmostEquals(Tuple.VZ, savedRay.direction)
    }

    @Test
    fun `Normal on translated shape`() {
        val t = Matrix.translate(0, 1, 0)
        val s = TestShape(t)
        val n = s.normalAt(Tuple.point(0, 1.70711, -0.70711))
        assertAlmostEquals(Tuple.vector(0, 0.70711, -0.70711), n)
    }

    @Test
    fun `Normal on transformed shape`() {
        val t = Matrix.scale(1, 0.5, 1) * Matrix.rotateZ(PI/5)
        val s = TestShape(t)
        val n = s.normalAt(Tuple.point(0, sqrt2by2, -sqrt2by2))
        assertAlmostEquals(Tuple.vector(0, 0.97014, -0.24254), n)
    }

    @Test
    fun `Point from world to local space`() {
        val s = Sphere(Matrix.translate(5, 0, 0))
        val g2 = Group(listOf(s), Matrix.scale(2, 2, 2))
        val g1 = Group(listOf(g2), Matrix.rotateY(PI / 2))

        // We have to get the new sphere to have the parent set.
        val sNew = (g1.children[0] as Group).children[0]
        val p = sNew.worldToLocal(Tuple.point(-2, 0, -10))
        assertAlmostEquals(Tuple.point(0, 0, -1), p)
    }

    @Test
    fun `Normal from local to world space`() {
        val s = Sphere(Matrix.translate(5, 0, 0))
        val g2 = Group(listOf(s), Matrix.scale(1, 2, 3))
        val g1 = Group(listOf(g2), Matrix.rotateY(PI / 2))

        // We have to get the new sphere to have the parent set.
        val sNew = (g1.children[0] as Group).children[0]
        val sqrt3by3 = sqrt(3.0) / 3
        val n = sNew.normalToWorld(Tuple.vector(sqrt3by3, sqrt3by3, sqrt3by3))
        assertAlmostEquals(Tuple.vector(0.28571, 0.42857, -0.85714), n)
    }

    @Test
    fun `Normal on an object in group`() {
        val s = Sphere(Matrix.translate(5, 0, 0))
        val g2 = Group(listOf(s), Matrix.scale(1, 2, 3))
        val g1 = Group(listOf(g2), Matrix.rotateY(PI / 2))

        val sp = (g1.children[0] as Group).children[0]
        val n = sp.normalAt(Tuple.point(1.7321, 1.1547, -5.5774))
        assertAlmostEquals(Tuple.vector(0.28570, 0.42854, -0.85716), n)
    }

    @Test
    fun `Test shape has bounding box`() {
        val s = TestShape()
        assertEquals(Tuple.point(-1, -1, -1), s.bounds.minPoint)
        assertEquals(Tuple.point(1, 1, 1), s.bounds.maxPoint)
    }

    @Test
    fun `Shape bounding box in parent space`() {
        val s = Sphere(Matrix.translate(1, -3, 5) * Matrix.scale(0.5, 2, 4))
        assertAlmostEquals(Tuple.point(0.5, -5, 1), s.parentBounds.minPoint)
        assertAlmostEquals(Tuple.point(1.5, -1, 9), s.parentBounds.maxPoint)
    }
}
