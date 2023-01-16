package scene

// By Sebastian Raaphorst, 2023.

import math.*
import org.junit.jupiter.api.Test
import kotlin.math.PI

class CameraTest {
    @Test
    fun `Pixel size for horizontal canvas`() {
        val c = Camera(200, 125, PI/2)
        assertAlmostEquals(0.01, c.pixelSize)
    }

    @Test
    fun `Pixel size for vertical canvas`() {
        val c = Camera(125, 200, PI/2)
        assertAlmostEquals(0.01, c.pixelSize)
    }

    @Test
    fun `Construct ray through centre of canvas`() {
        val c = Camera(201, 101, PI/2)
        val r = c.rayForPixel(100, 50)
        assertAlmostEquals(Tuple.PZERO, r.origin)
        assertAlmostEquals(Tuple.vector(0, 0, -1), r.direction)
    }

    @Test
    fun `Construct ray through corner of canvas`() {
        val c = Camera(201, 101, PI/2)
        val r = c.rayForPixel(0, 0)
        assertAlmostEquals(Tuple.PZERO, r.origin)
        assertAlmostEquals(Tuple.vector(0.66519, 0.33259, -0.66851), r.direction)
    }

    @Test
    fun `Construct ray when camera is transformed`() {
        val c = Camera(201, 101, PI/2,
            Matrix.rotationY(PI/4) * Matrix.translate(0, -2, 5))
        val r = c.rayForPixel(100, 50)
        assertAlmostEquals(Tuple.point(0, 2, -5), r.origin)
        assertAlmostEquals(Tuple.vector(sqrt2by2, 0, -sqrt2by2), r.direction)
    }

    @Test
    fun `Render world with camera`() {
        val from = Tuple.point(0, 0, -5)
        val to = Tuple.PZERO
        val up = Tuple.VY
        val c = Camera(11, 11, PI/2, from.viewTransformationFrom(to, up))
        val image = c.render(World.DefaultWorld)
        assertAlmostEquals(Color(0.38066, 0.47583, 0.2855), image[5, 5])
    }
}
