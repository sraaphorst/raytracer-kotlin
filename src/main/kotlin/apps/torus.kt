package apps

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.light.PointLight
import org.vorpal.raytracer.math.Matrix
import org.vorpal.raytracer.math.Tuple
import org.vorpal.raytracer.scene.Camera
import org.vorpal.raytracer.scene.World
import org.vorpal.raytracer.shapes.Torus
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val torus = run {
        val t = Matrix.translate(0, -1.5, 3) * Matrix.rotateX(PI / 2) * Matrix.scale(0.5, 1, 0.25)
        Torus(2, 0.5, t)
    }

    val world = run {
        val light = PointLight(Tuple.point(-1, 3.5, -1))
        World(listOf(room, torus), listOf(light))
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
