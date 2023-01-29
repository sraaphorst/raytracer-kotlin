package shapes

// By Sebastian Raaphorst, 2023.

import math.Intersection
import math.Matrix
import math.Ray
import math.Tuple
import org.junit.jupiter.api.Test
import kotlin.test.*

class CSGShapeTest {
    @Test
    fun `CSG creation`() {
        val s1 = Sphere()
        val s2 = Cube()
        val csg = CSGShape(Operation.Union, s1, s2)

        assertEquals(Operation.Union, csg.operation)
        assertNotSame(s1, csg.left)
        assertIs<Sphere>(csg.left)
        assertNotSame(s2, csg.right)
        assertIs<Cube>(csg.right)
    }

    @Test
    fun `Rules for Union`() {
        assertFalse(Operation.Union.intersectionAllowed(true, true, true))
        assertTrue(Operation.Union.intersectionAllowed(true, true, false))
        assertFalse(Operation.Union.intersectionAllowed(true, false, true))
        assertTrue(Operation.Union.intersectionAllowed(true, false, false))
        assertFalse(Operation.Union.intersectionAllowed(false, true, true))
        assertFalse(Operation.Union.intersectionAllowed(false, true, false))
        assertTrue(Operation.Union.intersectionAllowed(false, false, true))
        assertTrue(Operation.Union.intersectionAllowed(false, false, false))
    }

    @Test
    fun `Rules for Intersection`() {
        assertTrue(Operation.Intersection.intersectionAllowed(true, true, true))
        assertFalse(Operation.Intersection.intersectionAllowed(true, true, false))
        assertTrue(Operation.Intersection.intersectionAllowed(true, false, true))
        assertFalse(Operation.Intersection.intersectionAllowed(true, false, false))
        assertTrue(Operation.Intersection.intersectionAllowed(false, true, true))
        assertTrue(Operation.Intersection.intersectionAllowed(false, true, false))
        assertFalse(Operation.Intersection.intersectionAllowed(false, false, true))
        assertFalse(Operation.Intersection.intersectionAllowed(false, false, false))
    }

    @Test
    fun `Rules for difference`() {
        assertFalse(Operation.Difference.intersectionAllowed(true, true, true))
        assertTrue(Operation.Difference.intersectionAllowed(true, true, false))
        assertFalse(Operation.Difference.intersectionAllowed(true, false, true))
        assertTrue(Operation.Difference.intersectionAllowed(true, false, false))
        assertTrue(Operation.Difference.intersectionAllowed(false, true, true))
        assertTrue(Operation.Difference.intersectionAllowed(false, true, false))
        assertFalse(Operation.Difference.intersectionAllowed(false, false, true))
        assertFalse(Operation.Difference.intersectionAllowed(false, false, false))
    }

    @Test
    fun `Filtering list of intersections`() {
        val s1 = Sphere()
        val s2 = Cube()

        val intersectionIndices = listOf(Pair(0, 3), Pair(1, 2), Pair(0, 1))
        listOf(Operation.Union, Operation.Intersection, Operation.Difference)
            .zip(intersectionIndices)
            .forEach { (op, indices) ->
                val (x0, x1) = indices
                val csg = CSGShape(op, s1, s2)
                val (s1P, s2P) = csg
                val xs = listOf(
                    Intersection(1, s1P), Intersection(2, s2P),
                    Intersection(3, s1P), Intersection(4, s2P)
                )

                val result = csg.filterIntersection(xs)
                assertEquals(2, result.size)
                assertEquals(result[0], xs[x0])
                assertEquals(result[1], xs[x1])
            }
    }

    @Test
    fun `Ray misses CSG object`() {
        val csg = CSGShape(Operation.Union, Sphere(), Cube())
        val ray = Ray(Tuple.point(0, -2, 5), Tuple.VZ)

        val xs = csg.localIntersect(ray)
        assertTrue(xs.isEmpty())
    }

    @Test
    fun `Ray hits CSG object`() {
        val s1 = Sphere()
        val s2 = Sphere(Matrix.translate(0, 0, 0.5))
        val csg = CSGShape(Operation.Union, s1, s2)
        val ray = Ray(Tuple.point(0, 0, -5), Tuple.VZ)

        val xs = csg.localIntersect(ray)
        assertEquals(2, xs.size)
        val (s1p, s2p) = csg
        assertEquals(4.0, xs[0].t)
        assertSame(s1p, xs[0].shape)
        assertEquals(6.5, xs[1].t)
        assertSame(s2p, xs[1].shape)
    }
}
