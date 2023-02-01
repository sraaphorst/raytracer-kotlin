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
    val cube = run {
        val t = Matrix.translate(0, 1.5, 0) * Matrix.scale(2, 4, 8)
    }

    val plane = run {
        val t = Matrix.translate(0, -2.5, 0)
        val m = Material(CheckerPattern(Color(0.9, 0.4, 0.4), Color.WHITE), specular = 0)
        Plane(t, m)
    }

    val torus2 = run {
        val t = Matrix.translate(0, 0, 2) * Matrix.rotateX(PI / 2)
        Torus(2, 0.5, t)
    }

    val world = run {
        val light = PointLight(Tuple.point(0, 10, -5))
        World(listOf(plane, torus2), listOf(light))
    }

    val camera = run {
        val from = Tuple.point(0, 0, -5)
        val to = Tuple.PZERO
        val t = from.viewTransformationFrom(to, Tuple.VY)
        Camera(1000, 1000, 1, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/torus.ppm"))
    }
    println("Time elapsed (rendering): ${elapsed / 1000.0} s")
}
