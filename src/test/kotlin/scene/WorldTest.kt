package scene

// By Sebastian Raaphorst, 2023.

import light.PointLight
import material.Material
import math.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import pattern.PatternTest
import shapes.Plane
import shapes.Sphere
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorldTest {
    @Test
    fun `Default world`() {
        val w = World.DefaultWorld
        assertEquals(2, w.shapes.size)
        assertTrue(w.shapes.all { it in w })
        assertEquals(1, w.lights.size)
        assertEquals(PointLight(Tuple.point(-10, 10, -10)), w.lights[0])
    }

    @Test
    fun `Intersect default world with ray`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val xs = World.DefaultWorld.intersect(r)
        assertAlmostEquals(listOf(4, 4.5, 5.5, 6), xs.map { it.t })
    }

    @Test
    fun `Shading an intersection`() {
        val w = World.DefaultWorld
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val s = w.shapes[0]
        val x = Intersection(4, s)
        val comps = x.computations(r)
        val c = w.shadeHit(comps)
        assertAlmostEquals(Color(0.38066, 0.47583, 0.2855), c)
    }

    @Test
    fun `Shading an intersection from the inside`() {
        val light = PointLight(Tuple.point(0, 0.25, 0))
        val w = World(World.DefaultWorld.shapes, light)

        val r = Ray(Tuple.PZERO, Tuple.VZ)
        val s = w.shapes[1]
        val x = Intersection(0.5, s)
        val comps = x.computations(r)
        val c = w.shadeHit(comps)
        assertAlmostEquals(Color(0.90498, 0.90498, 0.90498), c)
    }

    @Test
    fun `Color when a ray misses`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VY)
        val c = World.DefaultWorld.colorAt(r)
        assertEquals(Color.BLACK, c)
    }

    @Test
    fun `Color when a ray hits`() {
        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val c = World.DefaultWorld.colorAt(r)
        assertAlmostEquals(Color(0.38066, 0.47583, 0.2855), c)
    }

    @Test
    fun `Color with intersection behind the ray`() {
        val light = PointLight(Tuple.point(-10, 10, -10))
        val m1 = Material(Color(0.8, 1.0, 0.6), 1.0, 0.7, 0.2)
        val s1 = Sphere(material = m1)
        val m2 = Material(ambient = 1.0)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5), m2)
        val w = World(listOf(s1, s2), light)

        val r = Ray(Tuple.point(0, 0, 0.75), Tuple.vector(0, 0, -1))
        val c = w.colorAt(r)

        // m2 should have a SolidColor pattern with white.
        assertAlmostEquals(Color.WHITE, c)
    }

    @Test
    fun `No shadow when nothing is collinear with point and light`() {
        val w = World.DefaultWorld
        val p = Tuple.point(0, 10, 0)
        assertFalse(w.isShadowed(p, w.lights.first()))
    }

    @Test
    fun `Shadow when an object is between point and light`() {
        val w = World.DefaultWorld
        val p = Tuple.point(10, -10, 10)
        assertTrue(w.isShadowed(p, w.lights.first()))
    }

    @Test
    fun `No shadow when object behind light`() {
        val w = World.DefaultWorld
        val p = Tuple.point(-20, 20, -20)
        assertFalse(w.isShadowed(p, w.lights.first()))
    }

    @Test
    fun `No shadow when object is behind the point`() {
        val w = World.DefaultWorld
        val p = Tuple.point(-2, 2, -2)
        assertFalse(w.isShadowed(p, w.lights.first()))
    }

    @Test
    fun `shadeHit is given intersection in shadow`() {
        val light = PointLight(Tuple.point(0, 0, -10))
        val s1 = Sphere()
        val s2 = Sphere(Matrix.translate(0, 0, 10))
        val w = World(listOf(s1, s2), light)

        val r = Ray(Tuple.point(0, 0, 5), Tuple.VZ)
        val x = Intersection(4, s2)
        val comps = x.computations(r)
        val c = w.shadeHit(comps)

        assertAlmostEquals(Color(0.1, 0.1, 0.1), c)
    }

    @Test
    fun `Reflected color for non-reflective material`() {
        val light = PointLight(Tuple.point(-10, 10, -10))
        val m1 = Material(Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)
        val s1 = Sphere(material = m1)
        val m2 = Material(ambient = 1.0)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5), m2)
        val w = World(listOf(s1, s2), light)

        val r = Ray(Tuple.PZERO, Tuple.VZ)
        val x = Intersection(1, s2)
        val comps = x.computations(r)
        val c = w.reflectedColor(comps)

        assertAlmostEquals(Color.BLACK, c)
    }

    @Test
    fun `Reflected color and shadeHit for reflective material`() {
        val light = PointLight(Tuple.point(-10, 10, -10))
        val m1 = Material(Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)
        val s1 = Sphere(material = m1)
        val m2 = Material(ambient = 1.0)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5), m2)
        val m3 = Material(reflectivity = 0.5)
        val s3 = Plane(Matrix.translate(0, -1, 0), m3)
        val w = World(listOf(s1, s2, s3), light)

        val r = Ray(Tuple.point(0, 0, -3), Tuple.vector(0, -sqrt2by2, sqrt2by2))
        val x = Intersection(sqrt2, s3)
        val comps = x.computations(r)

        val c1 = w.reflectedColor(comps)
        assertAlmostEquals(Color(0.19033, 0.23791, 0.14274), c1)

        val c2 = w.shadeHit(comps)
        assertAlmostEquals(Color(0.87675, 0.92434, 0.82917), c2)
    }

    @Test
    fun `colorAt with mutually reflective surfaces`() {
        val light = PointLight(Tuple.PZERO)
        val lower = Plane(Matrix.translate(0, -1,0), Material(reflectivity = 1.0))
        val upper = Plane(Matrix.translate(0, 1, 0), Material(reflectivity = 1.0))

        val w = World(listOf(lower, upper), light)
        val r = Ray(Tuple.PZERO, Tuple.VY)
        assertDoesNotThrow { w.colorAt(r) }
    }

    @Test
    fun `Reflected color at maximum recursive depth`() {
        val light = PointLight(Tuple.point(-10, 10, -10))
        val m1 = Material(Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)
        val s1 = Sphere(material = m1)
        val m2 = Material(ambient = 1.0)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5), m2)
        val m3 = Material(reflectivity = 0.5)
        val s3 = Plane(Matrix.translate(0, -1, 0), m3)
        val w = World(listOf(s1, s2, s3), light)

        val r = Ray(Tuple.point(0, 0, -3), Tuple.vector(0, -sqrt2by2, sqrt2by2))
        val x = Intersection(sqrt2, s3)
        val comps = x.computations(r)
        val c = w.reflectedColor(comps, 0)
        assertEquals(Color.BLACK, c)
    }

    @Test
    fun `Refracted color with an opaque surface`() {
        val w = World.DefaultWorld
        val s = w.shapes.first()

        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val xs = listOf(Intersection(4, s), Intersection(6, s))
        val comps = xs[0].computations(r, xs)
        val c = w.refractedColor(comps, 5)
        assertEquals(Color.BLACK, c)
    }

    @Test
    fun `Refracted color at maximum recursive depth`() {
        val light = PointLight(Tuple.point(-10, 10, -10))
        val m1 = Material(Color(0.8, 1.0, 0.6),
            diffuse = 0.7, specular = 0.2, transparency = 1.0, refractiveIndex = 1.5)
        val s1 = Sphere(material = m1)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5))
        val w = World(listOf(s1, s2), light)

        val r = Ray(Tuple.point(0, 0, -5), Tuple.VZ)
        val xs = listOf(Intersection(4, s1), Intersection(6, s1))
        val comps = xs[0].computations(r, xs)
        val c = w.refractedColor(comps, 0)
        assertEquals(Color.BLACK, c)
    }

    @Test
    fun `Refracted color under total internal reflection`() {
        val light = PointLight(Tuple.point(-10, 10, -10))
        val m1 = Material(Color(0.8, 1.0, 0.6),
            diffuse = 0.7, specular = 0.2, transparency = 1.0, refractiveIndex = 1.5)
        val s1 = Sphere(material = m1)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5))
        val w = World(listOf(s1, s2), light)

        val r = Ray(Tuple.point(0, 0, sqrt2by2), Tuple.VY)
        val xs = listOf(Intersection(-sqrt2by2, s1), Intersection(sqrt2by2, s1))

        // We are inside the sphere, so we need to look at xs[1], not xs[0].
        val comps = xs[1].computations(r, xs)
        val c = w.refractedColor(comps)
        assertEquals(Color.BLACK, c)
    }

    @Test
    fun `Refracted color with refracted ray`() {
        val light = PointLight(Tuple.point(-10, 10, -10))
        val m1 = Material(PatternTest.TestPattern(), ambient = 1.0)
        val s1 = Sphere(material = m1)
        val m2 = Material(transparency = 1.0, refractiveIndex = 1.5)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5), m2)
        val w = World(listOf(s1, s2), light)

        val r = Ray(Tuple.point(0, 0, 0.1), Tuple.VY)
        val xs = listOf(
            Intersection(-0.9899, s1), Intersection(-0.4899, s2),
            Intersection(0.4899, s2), Intersection(0.9899, s1)
        )
        val comps = xs[2].computations(r, xs)
        val c = w.refractedColor(comps)
        assertAlmostEquals(Color(0, 0.99887, 0.04721), c)
    }

    @Test
    fun `shadeHit with a transparent material`() {
        val light = PointLight(Tuple.point(-10, 10, -10))
        val m1 = Material(Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)
        val s1 = Sphere(material = m1)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5))
        val plane = Plane(Matrix.translate(0, -1, 0), Material(transparency = 0.5, refractiveIndex = 1.5))
        val ball = Sphere(Matrix.translate(0, -3.5, -0.5), Material(Color(1, 0, 0), ambient = 0.5))
        val w = World(listOf(s1, s2, plane, ball), light)

        val r = Ray(Tuple.point(0, 0, -3), Tuple.vector(0, -sqrt2by2, sqrt2by2))
        val xs = listOf(Intersection(sqrt2, plane))
        val comps = xs[0].computations(r, xs)
        val c = w.shadeHit(comps)
        assertAlmostEquals(Color(0.93642, 0.68642, 0.68642), c)
    }

    @Test
    fun `shadeHit with a reflective transparent material`() {
        val light = PointLight(Tuple.point(-10, 10, -10))
        val m1 = Material(Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)
        val s1 = Sphere(material = m1)
        val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5))
        val plane = Plane(Matrix.translate(0, -1, 0),
            Material(reflectivity = 0.5, transparency = 0.5, refractiveIndex = 1.5))
        val ball = Sphere(Matrix.translate(0, -3.5, -0.5), Material(Color(1, 0, 0), ambient = 0.5))
        val w = World(listOf(s1, s2, plane, ball), light)

        val r = Ray(Tuple.point(0, 0, -3), Tuple.vector(0, -sqrt2by2, sqrt2by2))
        val xs = listOf(Intersection(sqrt2, plane))
        val comps = xs[0].computations(r, xs)
        val c = w.shadeHit(comps)
        assertAlmostEquals(Color(0.93391, 0.69643, 0.69243), c)
    }
}
