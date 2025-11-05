package apps

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.light.PointLight
import org.vorpal.raytracer.math.Matrix
import org.vorpal.raytracer.math.Tuple
import org.vorpal.raytracer.shapes.Fusiform
import org.vorpal.raytracer.scene.Camera
import org.vorpal.raytracer.scene.World
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val fusiform = Fusiform(
        0.75, 0.25, -1.0, 1.0,
        Matrix.translate(0, -1.5, 0)
    )

    val world = run {
        val light = PointLight(Tuple.point(0, 3, -3))
        World(listOf(room, fusiform), listOf(light))
    }

    val camera = run {
        val from = Tuple.point(0, -1.5, -4)
        val to = Tuple.point(0, -1.5, 0)
        val t = from.viewTransformationFrom(to, Tuple.VY)
        Camera(1000, 2000, 1, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/fusiform.ppm"))
    }
    println("Time elapsed (rendering): ${elapsed / 1000.0} s")
}
