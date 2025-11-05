package apps

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.light.PointLight
import org.vorpal.raytracer.math.Matrix
import org.vorpal.raytracer.math.Tuple
import org.vorpal.raytracer.scene.Camera
import org.vorpal.raytracer.scene.World
import org.vorpal.raytracer.shapes.die6
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val die = die6().withTransformation(
        Matrix.rotateY(PI) * Matrix.rotateX(-PI / 4) * Matrix.rotateY(PI / 4))

    val world = run {
        val light1 = PointLight(Tuple.point(-3, 10, -5))
        val light2 = PointLight(Tuple.point(3, 10, -5))
        val light3 = PointLight(Tuple.point(-3, -10, -5))
        val light4 = PointLight(Tuple.point(3, -10, -5))
        World(listOf(die), listOf(light1, light2, light3, light4))
    }

    val camera = run {
        val from = Tuple.point(0, 0, -5)
        val to = Tuple.PZERO
        val t = from.viewTransformationFrom(to, Tuple.VY)
        Camera(800, 800, 1, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/die.ppm"))
    }
    println("Time elapsed: ${elapsed / 1000.0} s")
}