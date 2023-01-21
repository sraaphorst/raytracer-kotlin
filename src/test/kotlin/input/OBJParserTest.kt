package input

// By Sebastian Raaphorst, 2023.

import math.Tuple
import math.assertAlmostEquals
import org.junit.jupiter.api.Test
import shapes.Triangle
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OBJParserTest {
    @Test
    fun `Ignore unrecognized lines`() {
        val parser = OBJParser.fromURL(object{}.javaClass.getResource("/gibberish.obj"))
        assertEquals(5, parser.failedLines.size)
    }

    @Test
    fun `Vertex records`() {
        val parser = OBJParser.fromURL(object{}.javaClass.getResource("/points.obj"))
        assertEquals(0, parser.failedLines.size)
        assertEquals(4, parser.points.size)
        assertEquals(Tuple.point(-1, 1, 0), parser.points[1])
        assertEquals(Tuple.point(-1, 0.5, 0), parser.points[2])
        assertEquals(Tuple.point(1, 0, 0), parser.points[3])
        assertEquals(Tuple.point(1, 1, 0), parser.points[4])
    }

    @Test
    fun `Triangle faces`() {
        val parser = OBJParser.fromURL(object{}.javaClass.getResource("/faces.obj"))
        val group = parser.groups.getValue(OBJParser.DefaultGroup)
        val points = parser.points

        assertEquals(2, group.size)
        val t1 = group[0] as Triangle
        val t2 = group[1] as Triangle
        assertAlmostEquals(points.getValue(1), t1.p1)
        assertAlmostEquals(points.getValue(2), t1.p2)
        assertAlmostEquals(points.getValue(3), t1.p3)
        assertAlmostEquals(points.getValue(1), t2.p1)
        assertAlmostEquals(points.getValue(3), t2.p2)
        assertAlmostEquals(points.getValue(4), t2.p3)
    }

    @Test
    fun `Triangulating polygons`() {
        val parser = OBJParser.fromURL(object{}.javaClass.getResource("/polygon.obj"))
        val points = parser.points
        assertEquals(5, points.size)

        assertTrue(OBJParser.DefaultGroup in parser.groups)
        val group = parser.groups.getValue(OBJParser.DefaultGroup)

        assertEquals(3, group.size)
        val t1 = group[0] as Triangle
        val t2 = group[1] as Triangle
        val t3 = group[2] as Triangle
        assertAlmostEquals(points.getValue(1), t1.p1)
        assertAlmostEquals(points.getValue(2), t1.p2)
        assertAlmostEquals(points.getValue(3), t1.p3)
        assertAlmostEquals(points.getValue(1), t2.p1)
        assertAlmostEquals(points.getValue(3), t2.p2)
        assertAlmostEquals(points.getValue(4), t2.p3)
        assertAlmostEquals(points.getValue(1), t3.p1)
        assertAlmostEquals(points.getValue(4), t3.p2)
        assertAlmostEquals(points.getValue(5), t3.p3)
    }
    
    @Test
    fun `Triangles in groups`() {
        val parser = OBJParser.fromURL(object{}.javaClass.getResource("/triangles.obj"))
        val points = parser.points
        assertEquals(4, points.size)
        assertEquals(2, parser.groups.size)

        val g1Name = "FirstGroup"
        assertTrue(g1Name in parser.groups)
        val g1 = parser.groups.getValue(g1Name)
        assertEquals(1, g1.size)

        val t1 = g1[0] as Triangle
        assertAlmostEquals(points.getValue(1), t1.p1)
        assertAlmostEquals(points.getValue(2), t1.p2)
        assertAlmostEquals(points.getValue(3), t1.p3)

        val g2Name = "SecondGroup"
        assertTrue(g2Name in parser.groups)
        val g2 = parser.groups.getValue("SecondGroup")
        assertEquals(1, g2.size)

        val t2 = g2[0] as Triangle
        assertAlmostEquals(points.getValue(1), t2.p1)
        assertAlmostEquals(points.getValue(3), t2.p2)
        assertAlmostEquals(points.getValue(4), t2.p3)
    }
}
