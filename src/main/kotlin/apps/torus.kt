package apps

// By Sebastian Raaphorst, 2023.

import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import pattern.CheckerPattern
import scene.Camera
import scene.World
import shapes.Cube
import shapes.Torus
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val cube = run {
        val t = Matrix.scale(6, 4, 8)
        val p = CheckerPattern(Color(0.9, 0.4, 0.4), Color.WHITE, Matrix.scale(0.5, 0.5, 0.5))
        val m = Material(p, specular = 0)
        Cube(t, m)
    }

    val torus = run {
        val t = Matrix.translate(0, -1.5, 3) * Matrix.rotateX(PI / 2)
        Torus(2, 0.5, t)
    }

    val world = run {
        val light = PointLight(Tuple.point(-1, 3.5, -1))
        World(listOf(cube, torus), listOf(light))
    }

    val camera = run {
        val from = Tuple.point(0, -1.5, -4)
        val to = Tuple.point(0, -1.5, 0)
        val t = from.viewTransformationFrom(to, Tuple.VY)
        Camera(1000, 1000, 1, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/torus.ppm"))
    }
    println("Time elapsed (rendering): ${elapsed / 1000.0} s")
}
