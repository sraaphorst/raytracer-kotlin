package math

// By Sebastian Raaphorst, 2023.

import org.junit.jupiter.api.Test
import shapes.Plane
import shapes.Sphere
import kotlin.math.sqrt
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IntersectionTest {
    companion object {
        val s = Sphere()
    }

    @Test
    fun `Intersection encapsulates t and shape`() {
        val i = Intersection(3.5, s)
        assertEquals(3.5, i.t)
        assertEquals(s, i.shape)
    }

    @Test
    fun `hit when all intersections have positive t`() {
        val i1 = Intersection(1, s)
        val i2 = Intersection(2, s)
        val xs = intersections(i1, i2)
        assertEquals(i1, xs.hit())
    }

    @Test
    fun `hit when some intersections have negative t`() {
        val i1 = Intersection(-1, s)
        val i2 = Intersection(1, s)
        val xs = intersections(i1, i2)
        assertEquals(i2, xs.hit())
    }

    @Test
    fun `hit when all intersections have negative t`() {
        val i1 = Intersection(-2, s)
        val i2 = Intersection(-1, s)
        val xs = intersections(i1, i2)
        assertEquals(null, xs.hit())
    }

    @Test
    fun `hit is always min positive intersection`() {
        val i1 = Intersection(5, s)
        val i2 = Intersection(7, s)
        val i3 = Intersection(-3, s)
        val i4 = Intersection(2, s)
        val xs = intersections(i1, i2, i3, i4)
        assertEquals(i4, xs.hit())
    }

    @Test
    fun `Computing intersection state`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val x = Intersection(4, s)
        val comps = x.computations(r)
        assertAlmostEquals(x.t, comps.t)
        assertAlmostEquals(Tuple.point(0, 0, -1), comps.point)
        assertAlmostEquals(Tuple.vector(0, 0, -1), comps.eyeV)
        assertAlmostEquals(Tuple.vector(0, 0, -1), comps.normalV)
    }

    @Test
    fun `Hit when intersection occurs on the outside`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val x = Intersection(4, s)
        val comps = x.computations(r)
        assertFalse(comps.inside)
    }

    @Test
    fun `Hit when intersection occurs on inside`() {
        val r = Ray(Tuple.PZERO, Tuple.VZ)
        val x = Intersection(1, s)
        val comps = x.computations(r)
        assertAlmostEquals(Tuple.PZ, comps.point)
        assertAlmostEquals(Tuple.vector(0, 0, -1), comps.eyeV)
        assertTrue(comps.inside)
        assertAlmostEquals(Tuple.vector(0, 0, -1), comps.normalV)
    }

    @Test
    fun `Hit should offset the point`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val s = Sphere(Matrix.translate(0, 0, 1))
        val x = Intersection(5, s)
        val comps = x.computations(r)
        assertTrue(comps.overPoint.z < -DEFAULT_PRECISION/2)
        assertTrue(comps.point.z > comps.overPoint.z)
    }

    @Test
    fun `Compute reflection vector`() {
        val s = Plane()
        val sqrt2by2 = sqrt(2.0) / 2
        val r = Ray(Tuple.point(0, 1, -1), Tuple.vector(0, -sqrt2by2, sqrt2by2))
        val x = Intersection(sqrt(2.0), s)
        val comps = x.computations(r)
        assertAlmostEquals(Tuple.vector(0, sqrt2by2, sqrt2by2), comps.reflectV)
    }

    @Test
    fun `Compute n1 and n2 at various intersections`() {
        val s1 = Sphere.glassSphere(Matrix.scale(2, 2, 2), refractiveIndex = 1.5)
        val s2 = Sphere.glassSphere(Matrix.translate(0, 0, -0.25), refractiveIndex = 2.0)
        val s3 = Sphere.glassSphere(Matrix.translate(0, 0, 2.5), refractiveIndex = 2.5)

        val r = Ray(Tuple.point(0, 0, -4), Tuple.VZ)
        val xs = listOf(
            Intersection(2, s1),
            Intersection(2.75, s2),
            Intersection(3.25, s3),
            Intersection(4.75, s2),
            Intersection(5.25, s3),
            Intersection(6, s1))

        val n1s = listOf(1.0, 1.5, 2.0, 2.5, 2.5, 1.5)
        val n2s = listOf(1.5, 2.0, 2.5, 2.5, 1.5, 1.0)

        xs.zip(n1s.zip(n2s)).forEach { (x, ns) ->
            val (n1, n2) = ns
            val comps = x.computations(r, xs)
            assertAlmostEquals(n1, comps.n1)
            assertAlmostEquals(n2, comps.n2)
        }
    }

    @Test
    fun `underPoint is offset below the surface`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val s = Sphere.glassSphere(Matrix.translate(0, 0, 1))
        val x = Intersection(5, s)
        val xs = intersections(x)
        val comps = x.computations(r, xs)
        assertTrue(comps.underPoint.z > DEFAULT_PRECISION/2)
        assertTrue(comps.point.z < comps.underPoint.z)
    }

    @Test
    fun `Schlick approximation under total internal reflection`() {
        val s = Sphere.glassSphere()
        val r = Ray(Tuple.point(0, 0, sqrt2by2), Tuple.VY)
        val xs = intersections(Intersection(-sqrt2by2, s), Intersection(sqrt2by2, s))
        val comps = xs[1].computations(r, xs)
        val reflectance = comps.schlick
        assertEquals(1.0, reflectance)
    }

    @Test
    fun `Reflectance of a perpendicular ray`() {
        val s = Sphere.glassSphere()
        val r = Ray(Tuple.PZERO, Tuple.VY)
        val xs = intersections(Intersection(-1, s), Intersection(1, s))
        val comps = xs[1].computations(r, xs)
        val reflectance = comps.schlick
        assertAlmostEquals(0.04, reflectance)
    }

    @Test
    fun `Schlick approximation with small angle and n2 larger than n1`() {
        val s = Sphere.glassSphere()
        val r = Ray(Tuple.point(0, 0.99, -2), Tuple.VZ)
        val xs = intersections(Intersection(1.8589, s))
        val comps = xs[0].computations(r, xs)
        val reflectance = comps.schlick
        assertAlmostEquals(0.48873, reflectance)
    }
}
