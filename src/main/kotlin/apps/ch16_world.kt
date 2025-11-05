package apps

// By Sebastian Raaphorst, 2023.
// Adapted from https://github.com/lerouxrgd/raytracer/blob/master/samples/scenes/csg.yaml

import org.vorpal.raytracer.light.PointLight
import org.vorpal.raytracer.material.Material
import org.vorpal.raytracer.math.Color
import org.vorpal.raytracer.math.Matrix
import org.vorpal.raytracer.math.Tuple
import org.vorpal.raytracer.shapes.CSGShape
import org.vorpal.raytracer.shapes.Cube
import org.vorpal.raytracer.shapes.Cylinder
import org.vorpal.raytracer.shapes.Operation
import org.vorpal.raytracer.shapes.Sphere
import org.vorpal.raytracer.scene.Camera
import org.vorpal.raytracer.scene.World
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val csg = run {
        val outer = run {
            val s1 = Sphere(Matrix.scale(1.3, 1.3, 1.3))
            val s2 = Cube(Matrix.rotateY(PI / 3))
            CSGShape(Operation.Intersection, s1, s2)
        }

        val inner = run {
            val innerS1 = run {
                val s1 = Cylinder(
                    transformation = Matrix.rotateY(PI / 3) * Matrix.scale(0.6, 0.6, 0.6),
                    material = Material(Color.RED)
                )
                val s2 = Cylinder(
                    transformation = Matrix.rotateY(PI / 3) * Matrix.rotateX(PI / 2) *
                            Matrix.scale(0.6, 0.6, 0.6),
                    material = Material(Color.BLUE)
                )
                CSGShape(Operation.Union, s1, s2)
            }

            val innerS2 = Cylinder(
                transformation = Matrix.rotateY(PI / 3) * Matrix.rotateZ(PI / 2) *
                        Matrix.scale(0.6, 0.6, 0.6),
                material = Material(Color.GREEN)
            )

            CSGShape(Operation.Union, innerS1, innerS2)
        }

        CSGShape(Operation.Difference, outer, inner)
    }

    val world = run {
        val light = PointLight(Tuple.point(-10, 10, -10))
        World(listOf(csg), listOf(light))
    }

    val camera = run {
        val from = Tuple.point(0, 1.5, -5)
        val to = Tuple.PZERO
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        Camera(1280, 720, PI / 3, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/ch16_world.ppm"))
    }
    println("Time elapsed (rendering): ${elapsed / 1000.0} s")
}
