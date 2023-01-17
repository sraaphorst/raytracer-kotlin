package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import org.junit.jupiter.api.Test
import kotlin.math.PI

class ShapeTest {
    class TestShape(transformation: Matrix = Matrix.I,
                    material: Material = Material()): Shape(transformation, material) {
        // We need to use a var here to store a ray.
        var savedRay = Ray(Tuple.PZERO, Tuple.VZERO)

        override fun localIntersect(rayLocal: Ray): List<Intersection> {
            savedRay = rayLocal
            return emptyList()
        }

        override fun localNormalAt(localPoint: Tuple): Tuple =
            localPoint - Tuple.PZERO
    }

    @Test
    fun `Intersecting scaled shape with ray`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val t = Matrix.scale(2, 2, 2)
        val s = TestShape(t)
        s.intersect(r)
        assertAlmostEquals(Tuple.point(0, 0, -2.5), s.savedRay.origin)
        assertAlmostEquals(Tuple.vector(0, 0, 0.5), s.savedRay.direction)
    }

    @Test
    fun `Intersecting translated shape with ray`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val t = Matrix.translate(5, 0, 0)
        val s = TestShape(t)
        s.intersect(r)
        assertAlmostEquals(Tuple.point(-5, 0, -5), s.savedRay.origin)
        assertAlmostEquals(Tuple.VZ, s.savedRay.direction)
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
}
