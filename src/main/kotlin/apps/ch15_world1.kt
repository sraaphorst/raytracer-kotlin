package apps

// By Sebastian Raaphorst, 2023.

import input.OBJParser
import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import pattern.RingPattern
import scene.Camera
import scene.World
import shapes.Plane
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val bearStart = System.currentTimeMillis()
    val bear = OBJParser.fromURL({}.javaClass.getResource("/teddybear.obj"))
        .groups.getValue(OBJParser.DefaultGroup)

    val bear1 = run {
        val t = Matrix.translate(0, 3.26, 1.8) * Matrix.rotateY(PI) * Matrix.scale(0.15, 0.15, 0.15)
        val m = Material(Color(0.5, 0.5, 0.5), specular = 0)
        bear.withTransformation(t).withMaterial(m)
    }
    println("Time elapsed (processing bear): ${(System.currentTimeMillis() - bearStart) / 1000.0} s")

    val floorboards = run {
        val m = Material(RingPattern(Color(0.5, 0.25, 0.25), Color(0.25, 0.5, 0.5)))
        Plane(Matrix.I, m)
    }

    val leftWall = run {
        val t = Matrix.translate(0, 0, 15) *
                Matrix.rotateY(-PI / 4) *
                Matrix.rotateX(PI / 2) *
                Matrix.scale(10, 1, 10)
        val m = Material(Color(0.1, 0.25, 0.25))
        Plane(t, m)
    }

    val rightWall = run {
        val t= Matrix.translate(0, 0, 15) *
                Matrix.rotateY(PI / 4) *
                Matrix.rotateX(PI / 2)
        val m = Material(Color(0.75, 0.25, 0.25), specular = 0)
        Plane(t, m)
    }

    val world = run {
        val light = PointLight(Tuple.point(-10, 10, -10))
        World(listOf(floorboards, leftWall, rightWall, bear1), listOf(light))
    }


    val camera = run {
        val from = Tuple.point(0, 5, -5)
        val to = Tuple.PY
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        Camera(400, 400, PI / 2, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/ch15_world1.ppm"))
    }
    println("Time elapsed (rendering): ${elapsed / 1000.0} s")
}
