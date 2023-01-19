package apps

// By Sebastian Raaphorst, 2023.

import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import scene.Camera
import scene.World
import shapes.Sphere
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val wrist = run {
        val t = Matrix.rotateZ(PI/4) * Matrix.translate(-4, 0, -21) * Matrix.scale(3, 3, 3)
        val m = Material(Color(0.1, 1, 1), 0.2, 0.8, 0.3)
        Sphere(t, m)
    }

    val palm = run {
        val t = Matrix.translate(0, 0, -15) * Matrix.scale(4, 3, 3)
        val m = Material(Color(0.1, 0.1, 1), 0.2, 0.8, 0.3)
        Sphere(t, m)
    }

    val thumb = run {
        val t = Matrix.translate(-2, 2, -16) * Matrix.scale(1, 3, 1)
        val m = Material(Color(0.1, 0.1, 1), 0.2, 0.8, 0.3)
        Sphere(t, m)
    }

    val indexFinger = run {
        val t = Matrix.translate(3, 2, -22) * Matrix.scale(3, 0.75, 0.75)
        val m = Material(Color(1, 1, 0.1), 0.2, 0.8, 0.3)
        Sphere(t, m)
    }

    val middleFinger = run {
        val t = Matrix.translate(4, 1, -19) * Matrix.scale(3, 0.75, 0.75)
        val m = Material(Color(0.1, 1, 0.5), 0.2, 0.8, 0.3)
        Sphere(t, m)
    }

    val ringFinger = run {
        val t = Matrix.translate(4, 0, -18) * Matrix.scale(3, 0.75, 0.75)
        val m = Material(Color(0.1, 1, 0.1), 0.2, 0.8, 0.3)
        Sphere(t, m)
    }

    val pinkyFinger = run {
        val t = Matrix.translate(3, -1.5, -20) * Matrix.rotateZ(-PI/10) *
                Matrix.translate(1, 0, 0) * Matrix.scale(2.5, 0.6, 0.6)
        val m = Material(Color(0.1, 0.5, 1), 0.2, 0.8, 0.3)
        Sphere(t, m)
    }

    val backdrop = run {
        val t = Matrix.translate(0, 0, 20) * Matrix.scale(200, 200, 0.01)
        val m = Material(ambient = 0.0, diffuse = 0.5, specular = 0.0)
        Sphere(t, m)
    }

    val world = run {
        val shapes = listOf(wrist, palm, thumb, indexFinger, middleFinger, ringFinger, pinkyFinger, backdrop)
        val light = PointLight(Tuple.point(0, 0, -100))
        World(shapes, light)
    }

    val camera = run {
        val from = Tuple.point(40, 0, -70)
        val to = Tuple.point(0, 0, -5)
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        Camera(1200, 600, PI/6, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/ch08_shadowpuppet.ppm"))
    }
    println("Time elapsed: ${elapsed / 1000.0} s")
}
