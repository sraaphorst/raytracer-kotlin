package apps

// By Sebastian Raaphorst, 2023.
// Scene details provided by Jamis Buck, private communication.

import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import math.degreesToRadians
import pattern.CheckerPattern
import scene.Camera
import scene.World
import shapes.Cube
import shapes.Plane
import shapes.Torus
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val plane = run {
        val m = Material(CheckerPattern(Color.fromHex(0xff7f7f), Color.fromHex(0xffffff)))
        Plane(material = m)
    }

    val cube = run {
        val t = Matrix.translate(0, 0.9, 0) * Matrix.scale(10, 8, 10)
        val m = Material(specular = 0)
        Cube(t, m)
    }

    val torus = run {
        val t = Matrix.translate(0, 0.25, 0)
        val m = Material(Color.fromHex(0x111111),
            ambient = 0, diffuse = 0.1, specular = 0.9, shininess = 100, reflectivity = 0.9)
        Torus(0.75, 0.25, t, m)
    }

    val world = run {
        val light = PointLight(Tuple.point(-4, 7, -8))
        World(listOf(plane, cube, torus), listOf(light))
    }

    val camera = run {
        val from = Tuple.point(1, 3, -5)
        val to = Tuple.point(-1.5, 0, -0.5)
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        Camera(1600, 400, degreesToRadians(50), t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/torus2.ppm"))
    }
    println("Time elapsed (rendering): ${elapsed / 1000.0} s")
}
