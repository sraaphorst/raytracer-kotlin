package math

// By Sebastian Raaphorst, 2023.

import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.test.assertEquals

class BoundingBoxTest {
    @Test
    fun `Empty bounding box`() {
        val box = BoundingBox()
        assertEquals(BoundingBox.MaxPoint, box.minPoint)
        assertEquals(BoundingBox.MinPoint, box.maxPoint)
    }

    @Test
    fun `Bounding box with volume`() {
        val box = BoundingBox(Tuple.point(-1, -2, -3), Tuple.point(3, 2, 1))
        assertEquals(Tuple.point(-1, -2, -3), box.minPoint)
        assertEquals(Tuple.point(3, 2, 1), box.maxPoint)
    }

    @Test
    fun `Adding points to bounding box`() {
        val box = BoundingBox().add(Tuple.point(-5, 2, 0)).add(Tuple.point(7, 0, -3))
        assertEquals(Tuple.point(-5, 0, -3), box.minPoint)
        assertEquals(Tuple.point(7, 2, 0), box.maxPoint)
    }

    @Test
    fun `Merging bounding boxes`() {
        val box1 = BoundingBox(Tuple.point(-5, -2, 0), Tuple.point(7, 4, 4))
        val box2 = BoundingBox(Tuple.point(8, -7, -2), Tuple.point(14, 2, 8))

        // merge is a commutative operation.
        val box3 = box1.merge(box2)
        val box4 = box2.merge(box1)
        assertEquals(box3.minPoint, box4.minPoint)
        assertEquals(box3.maxPoint, box4.maxPoint)
        assertEquals(Tuple.point(-5, -7, -2), box3.minPoint)
        assertEquals(Tuple.point(14, 4, 8), box3.maxPoint)
    }

    @Test
    fun `Check if point in bounding box`() {
        val box = BoundingBox(Tuple.point(5, -2, 0), Tuple.point(11, 4, 7))

        val data = listOf(
            Tuple.point(5, -2, 0) to true,
            Tuple.point(11, 4, 7) to true,
            Tuple.point(8, 1, 3) to true,
            Tuple.point(3, 0, 3) to false,
            Tuple.point(8, -4, 3) to false,
            Tuple.point(8, 1, -1) to false,
            Tuple.point(13, 1, 3) to false,
            Tuple.point(8, 5, 3) to false,
            Tuple.point(8, 1, 8) to false
        )
        data.forEach { (point, isIn) ->
            assertEquals(isIn, point in box)
        }
    }

    @Test
    fun `Box contains other box`() {
        val box = BoundingBox(Tuple.point(5, -2, 0), Tuple.point(11, 4, 7))

        val data = listOf(
            BoundingBox(Tuple.point(5, -2, 0), Tuple.point(11, 4, 7)) to true,
            BoundingBox(Tuple.point(6, -1, 1), Tuple.point(10, 3, 6)) to true,
            BoundingBox(Tuple.point(4, -3, -1), Tuple.point(10, 3, 6)) to false,
            BoundingBox(Tuple.point(6, -1, 1), Tuple.point(12, 5, 8)) to false
        )
        data.forEach { (testBox, isIn) ->
            assertEquals(isIn, testBox in box)
        }
    }

    @Test
    fun `Transform bounding box`() {
        val box = BoundingBox(Tuple.point(-1, -1, -1), Tuple.point(1, 1, 1))
        val t = Matrix.rotateX(PI / 4) * Matrix.rotateY(PI / 4)

        val tBox = box.transform(t)
        assertAlmostEquals(Tuple.point(-1.41421, -1.70710, -1.70710), tBox.minPoint)
        assertAlmostEquals(Tuple.point(1.41421, 1.70710, 1.70710), tBox.maxPoint)
    }

    @Test
    fun `Intersecting ray with bounding box`() {
        val box = BoundingBox(Tuple.point(-1, -1, -1), Tuple.point(1, 1, 1))

        val data = listOf(
            Triple(Tuple.point(5, 0.5, 0), Tuple.vector(-1, 0, 0), true),
            Triple(Tuple.point(-5, 0.5, 0), Tuple.vector(1, 0, 0), true),
            Triple(Tuple.point(0.5, 5, 0), Tuple.vector(0, -1, 0), true),
            Triple(Tuple.point(0.5, -5, 0), Tuple.vector(0, 1, 0), true),
            Triple(Tuple.point(0.5, 0, 5), Tuple.vector(0, 0, -1), true),
            Triple(Tuple.point(0.5, 0, -5), Tuple.vector(0, 0, 1), true),
            Triple(Tuple.point(0, 0.5, 0), Tuple.vector(0, 0, 1), true),
            Triple(Tuple.point(-2, 0, 0), Tuple.vector(2, 4, 6), false),
            Triple(Tuple.point(0, -2, 0), Tuple.vector(6, 2, 4), false),
            Triple(Tuple.point(0, 0, -2), Tuple.vector(4, 6, 2), false),
            Triple(Tuple.point(2, 0, 2), Tuple.vector(0, 0, -1), false),
            Triple(Tuple.point(0, 2, 2), Tuple.vector(0, -1, 0), false),
            Triple(Tuple.point(2, 2, 0), Tuple.vector(-1, 0, 0), false)
        )
        data.forEach { (origin, direction, isIn) ->
            val ray = Ray(origin, direction.normalized)
            assertEquals(isIn, box.intersects(ray).isNotEmpty())
        }
    }

    @Test
    fun `Intersect ray with non-cubic bounding box`() {
        val box = BoundingBox(Tuple.point(5, -2, 0), Tuple.point(11, 4, 7))
        
        val data = listOf(
            Triple(Tuple.point(15, 1, 2), Tuple.vector(-1, 0, 0), true),
            Triple(Tuple.point(-5, -1, 4), Tuple.vector(1, 0, 0), true),
            Triple(Tuple.point(7, 6, 5), Tuple.vector(0, -1, 0), true),
            Triple(Tuple.point(9, -5, 6), Tuple.vector(0, 1, 0), true),
            Triple(Tuple.point(8, 2, 12), Tuple.vector(0, 0, -1), true),
            Triple(Tuple.point(6, 0, -5), Tuple.vector(0, 0, 1), true),
            Triple(Tuple.point(8, 1, 3.5), Tuple.vector(0, 0, 1), true),
            Triple(Tuple.point(9, -1, -8), Tuple.vector(2, 4, 6), false),
            Triple(Tuple.point(8, 3, -4), Tuple.vector(6, 2, 4), false),
            Triple(Tuple.point(9, -1, -2), Tuple.vector(4, 6, 2), false),
            Triple(Tuple.point(4, 0, 9), Tuple.vector(0, 0, -1), false),
            Triple(Tuple.point(8, 6, -1), Tuple.vector(0, -1, 0), false),
            Triple(Tuple.point(12, 5, 4), Tuple.vector(-1, 0, 0), false)
        )
        data.forEach { (origin, direction, isIn) ->
            val ray = Ray(origin, direction.normalized)
            assertEquals(isIn, box.intersects(ray).isNotEmpty())
        }
    }
}
