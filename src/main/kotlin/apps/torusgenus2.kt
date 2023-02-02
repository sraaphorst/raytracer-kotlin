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
import shapes.*
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

    // val t = Matrix.translate(0, -1.5, 3) * Matrix.rotateX(PI / 2)
    // Dimensions:
    // x: -1 to 1
    // y: -1 to 1
    // z: -(2 * innerRadius + outerRadius) to (2 * innerRadius + outerRadius)
    val innerRadius = 0.75
    val outerRadius = 0.25
    val torusGenus2 = run {
        val torus1 = run {
            val t = Matrix.translate(0, 0, innerRadius - outerRadius/10)
            Torus(innerRadius, outerRadius, t)
        }
        val torus2 = run {
            val t = Matrix.translate(0, 0, -innerRadius + outerRadius/10)
            Torus(innerRadius, outerRadius, t)
        }
        val torusIntersection = TorusIntersection(innerRadius, outerRadius, Matrix.translate(0, 0.1, 0))
        Group(listOf(torus1, torus2))//, torusIntersection))
    }

    // Shrinks so that -1 to 1 in z.
    val torusGenus2Scaler = run {
        val z = 2 * innerRadius + outerRadius
        Matrix.scale(1.0 / z, 1.0 / z, 1.0 / z)
    }

    val torusRotated = torusGenus2.withTransformation(Matrix.translate(0, -1.5, 0) * Matrix.rotateX(PI / 2))// * torusGenus2Scaler)

    val world = run {
        val light = PointLight(Tuple.point(0, 0, -2))
        World(listOf(cube, torusRotated), listOf(light))
    }

    val camera = run {
        val from = Tuple.point(0, -1.5, -4)
        val to = Tuple.point(0, -1.5, 0)
        val t = from.viewTransformationFrom(to, Tuple.VY)
        Camera(1000, 2000, 1, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/torusgenus2.ppm"))
    }
    println("Time elapsed (rendering): ${elapsed / 1000.0} s")
}
