package apps

// By Sebastian Raaphorst, 2023.

import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import pattern.*
import scene.Camera
import scene.World
import shapes.Plane
import shapes.Sphere
import java.io.File
import kotlin.math.PI

fun main() {
    val world = run {
        val floor = run {
            val p1 = StripedPattern(Color.BLUE, Color.WHITE,
                Matrix.rotateY(PI/4) * Matrix.scale(0.75, 0.75, 0.75))
            val p2 = StripedPattern(Color.WHITE, Color.GREEN,
                Matrix.rotateY(-PI/4) * Matrix.scale(0.75, 0.75, 0.75))
            val p = PerlinNoisePattern(BlendedPattern(p1, p2))
            val m = Material(p)
            Plane(Matrix.I, m)
        }

        val leftWall = run {
            val t = Matrix.translate(0, 0, 10) *
                    Matrix.rotateY(-PI / 4) *
                    Matrix.rotateX(PI / 2)
            val p1 = StripedPattern(Color(1, 0, 0), Color(1, 0.5, 0.5),
                Matrix.rotateY(PI/4) * Matrix.scale(0.1, 1, 1))
            val p2 = StripedPattern(Color(0, 0, 1), Color(0.5, 0.5, 1),
                Matrix.rotateY(-PI/4) * Matrix.scale(0.1, 1, 1))
            val p = CheckerPattern(p1, p2)
            val m = Material(p)
            Plane(t, m)
        }

        val rightWall = run {
            val t = Matrix.translate(0, 5, 10) *
                    Matrix.rotateY(PI / 4) *
                    Matrix.rotateX(PI / 2) *
                    Matrix.scale(0.15, 0.15, 0.15)
            val p1 = RingPattern(Color(1, 0.5, 0), Color(0.5, 1, 1),
                Matrix.translate(25, 0, 15))
            val p = SimplexNoisePattern(SimplexNoisePattern(p1))
            val m = Material(p, specular = 0.0)
            Plane(t, m)
        }

        // Middle sphere is a unit sphere, translated upward and slightly green.
        val middleSphere = run {
            val t = Matrix.translate(-0.25, 1, 1.5) * Matrix.rotateY(-PI/3) * Matrix.rotateZ(-PI/6)
            val p = StripedPattern(Color(0.25, 0.25, 0.8), Color(0.5, 0.5, 1),
                Matrix.scale(0.15, 1, 1))
            val m = Material(p, diffuse = 0.7, specular = 0.3)
            Sphere(t, m)
        }

        // Right sphere is a smaller green sphere scaled in half.
        val rightSphere = run {
            val t = Matrix.translate(1.5, 0.5, -0.5) *
                    Matrix.scale(0.5, 0.5, 0.5) //* Matrix.rotationY(PI/2)
            val p = GradientPattern(Color(0.25, 0, 0.25), Color(1.0, 0.5, 1.0),
                Matrix.rotateY(PI/4))
            val m = Material(p, diffuse = 0.7, specular = 0.3)
            Sphere(t, m)
        }

        // Left sphere is scaled by a third before being translated.
        val leftSphere = run {
            val t = Matrix.translate(-1.5, 0.66, -1) *
                    Matrix.scale(0.66, 0.66, 0.66)
            val p = CheckerPattern(Color.WHITE, Color(1, 0.25, 0.25),
                Matrix.scale(0.2, 0.2,0.2))
            val m = Material(p, diffuse = 0.7, specular = 0.3)
            Sphere(t, m)
        }

        // Light source is white, shining from above and to the left.
        val light = PointLight(Tuple.point(-10, 10, -10))
        World(listOf(floor, leftWall, rightWall, middleSphere, rightSphere, leftSphere), light)
    }

    // Create the camera.
    val camera = run {
        val from = Tuple.point(0, 1.5, -5)
        val to = Tuple.PY
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        Camera(2500, 1250, PI /3, t)
    }

    val canvas = camera.render(world)
    canvas.toPPMFile(File("output/ch10_world.ppm"))
}
