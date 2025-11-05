package apps

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.light.PointLight
import org.vorpal.raytracer.math.Matrix
import org.vorpal.raytracer.math.Tuple
import org.vorpal.raytracer.scene.Camera
import org.vorpal.raytracer.scene.World
import org.vorpal.raytracer.shapes.Group
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val hexagon = run {
        val sides = (0 until 6).map { Matrix.rotateY(it * PI / 3) }.map {
            side.withTransformation(it)
        }
        Group(sides, Matrix.rotateX(-0.4363) * Matrix.rotateY(-PI / 18))
    }

    val world = run {
        val light = PointLight(Tuple.point(0, 10, -5))
        World(listOf(hexagon), light)
    }

    val camera = run {
        val from = Tuple.point(0, 0, -5)
        val to = Tuple.PZERO
        val t = from.viewTransformationFrom(to, Tuple.VY)
        Camera(1200, 600, 1, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("org/vorpal/raytracer/output/ch14_hexagon.ppm"))
    }
    println("Time elapsed: ${elapsed / 1000.0} s")
}