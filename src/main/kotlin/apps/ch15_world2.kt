package apps

import input.OBJParser
import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import pattern.CheckerPattern
import scene.Camera
import scene.World
import shapes.Cube
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    // 49.156 points.
    // 49,152 quadrilaterals -> 98,304 triangles.
    val modelStart = System.currentTimeMillis()
    val model = OBJParser.fromURL({}.javaClass.getResource("/flaccid.obj"))
        .groups.getValue(OBJParser.DefaultGroup)

    val model1 = run {
        val t = Matrix.translate(0, 0, -5) * Matrix.rotateY(4 * PI / 5) *
                Matrix.translate(0, 3, 0) * Matrix.scale(0.05, 0.05,0.05)
        val m = Material(Color.fromHex(0xffe5b2), specular = 0, shininess = 36, transparency = 0)
        model.withTransformation(t).withMaterial(m)
    }
    println("Time elapsed (processing model): ${(System.currentTimeMillis() - modelStart) / 1000.0} s")

    val room = run {
        val m = Material(CheckerPattern(Color(0.25, 0.25, 0.25), Color(0.75, 0.75, 0.75),
            Matrix.scale(0.25, 0.25, 0.25)), specular = 0)
        val t = Matrix.scale(30, 30, 30)
        Cube(t, m)
    }

    val world = run {
        val light1 = PointLight(Tuple.point(-5, 10, -15), Color(0.6, 0.4, 0.4))
        val light2 = PointLight(Tuple.point(8, 15, -10), Color(0.4, 0.6, 0.4))
        World(listOf(room, model1), listOf(light1, light2))
    }

    val camera = run {
        val from = Tuple.point(0, 5, -25)
        val to = Tuple.point(0, 5, 0)
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        Camera(1200, 1200, PI / 2, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/ch15_world2.ppm"))
    }
    println("Time elapsed (rendering): ${elapsed / 1000.0} s")
}