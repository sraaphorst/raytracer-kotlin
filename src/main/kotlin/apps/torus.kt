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
import shapes.Plane
import shapes.Torus
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val plane = run {
        val m = Material(CheckerPattern(Color.RED, Color.WHITE))
        Plane(material = m)
    }

    val torus = run {
        val m = Material(Color(0.9, 0.9, 0.9), reflectivity = 0.9)
        val t = Matrix.translate(5, 1, 3)
        Torus(1, 1.5, t, m)
    }

    val world = run {
        val light = PointLight(Tuple.point(0, 5, 3))
        World(listOf(plane, torus), listOf(light))
    }

    val camera = run {
        val from = Tuple.point(0, 2, -1)
        val to = Tuple.PZERO
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        Camera(200, 50, PI / 2, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/torus.ppm"))
    }
    println("Time elapsed (rendering): ${elapsed / 1000.0} s")
}
