package apps

// By Sebastian Raaphorst, 2023.

import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import scene.Camera
import scene.World
import shapes.Plane
import shapes.Sphere
import java.io.File
import kotlin.math.PI

fun main() {
    val multipleLights = false

    val world = run {
        val floor = run {
            val t = Matrix.scale(10, 1, 10)
            val m = if (multipleLights) Material(Color.RED, specular = 0.0) else Material(specular = 0.0)
            Plane(t, m)
        }

        val leftWall = run {
            val t = Matrix.translate(0, 0, 5) *
                    Matrix.rotationY(-PI / 4) *
                    Matrix.rotationX(PI / 2) *
                    Matrix.scale(10, 1, 10)
            val m = if (multipleLights) Material(Color.GREEN, specular = 0.0) else Material(specular = 0.0)
            Plane(t, m)
        }

        val rightWall = run {
            val t = Matrix.translate(0, 0, 5) *
                    Matrix.rotationY(PI / 4) *
                    Matrix.rotationX(PI / 2) *
                    Matrix.scale(10, 1, 10)
            val m = if (multipleLights) Material(Color.BLUE, specular = 0.0) else Material(specular = 0.0)
            Plane(t, m)
        }

        // Middle sphere is a unit sphere, translated upward and slightly green.
        val middleSphere = run {
            val t = Matrix.translate(-0.5, 1, 0.5)
            val m = Material(Color(0.1, 1, 0.5), diffuse = 0.7, specular = 0.3)
            Sphere(t, m)
        }

        // Right sphere is a smaller green sphere scaled in half.
        val rightSphere = run {
            val t = Matrix.translate(1.5, 0.5, -0.5) *
                    Matrix.scale(0.5, 0.5, 0.5)
            val m = Material(Color(0.5, 1, 0.5), diffuse = 0.7, specular = 0.3)
            Sphere(t, m)
        }

        // Left sphere is scaled by a third before being translated.
        val leftSphere = run {
            val t = Matrix.translate(-1.5, 0.33, -0.75) *
                    Matrix.scale(0.33, 0.33, 0.33)
            val m = Material(Color(1, 0.8, 0.1), diffuse = 0.7, specular = 0.3)
            Sphere(t, m)
        }

        // Light source is white, shining from above and to the left.
        val lights = run {
            if (multipleLights)
                listOf(
                    PointLight(Tuple.point(-10, 10, -10), Color(0.25, 0.25, 0.25)),
                    PointLight(Tuple.point(10, 10, -10), Color(0.25, 0.25, 0.25)),
                    PointLight(Tuple.point(0, 10, -10), Color(0.25, 0.25, 0.25))
                )
            else
                listOf(PointLight(Tuple.point(-10, 10, -10)))
        }
        World(listOf(floor, leftWall, rightWall, middleSphere, rightSphere, leftSphere), lights)
    }

    // Create the camera.
    val camera = run {
        val from = Tuple.point(0, 1.5, -5)
        val to = Tuple.PY
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        Camera(1000, 500, PI / 3, t)
    }

    val canvas = camera.render(world)
    canvas.toPPMFile(File("output/ch09_world.ppm"))
}
