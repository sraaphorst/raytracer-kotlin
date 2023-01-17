package apps

// By Sebastian Raaphorst, 2023.
// From https://forum.raytracerchallenge.com/thread/6/tables-scene-description

import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import pattern.CheckerPattern
import pattern.StripedPattern
import scene.Camera
import scene.World
import shapes.Cube
import java.io.File

fun main() {
    val floorCeiling = run {
        val t = Matrix.scale(20, 7, 20) * Matrix.translate(0, 1, 0)
        val p = CheckerPattern(Color.BLACK, Color(0.25, 0.25, 0.25),
            Matrix.scale(0.07, 0.07, 0.07))
        val m = Material(p, ambient = 0.25, diffuse = 0.7, specular = 0.9, shininess = 300.0, reflectivity = 0.1)
        Cube(t, m)
    }

    val walls = run {
        val t = Matrix.scale(10, 10, 10)
        val p = CheckerPattern(Color(0.4863, 0.3765, 0.2941), Color(0.3725, 0.2902, 0.2275),
            Matrix.scale(0.05, 20, 0.05))
        val m = Material(p, ambient = 0.1, diffuse = 0.7, specular = 0.9, shininess = 300.0, reflectivity = 0.05)
        Cube(t, m)
    }

    val tableColor = Color(0.5529, 0.4235, 0.3255)

    val tableTop = run {
        val t = Matrix.translate(0, 3.1, 0) * Matrix.scale(3, 0.1, 2)
        val p = StripedPattern(tableColor, Color(0.6588, 0.5098, 0.4000),
            Matrix.scale(0.05, 0.05, 0.05) * Matrix.rotateY(0.1))
        val m = Material(p, ambient = 0.1, diffuse = 0.7, specular = 0.9, shininess = 300.0, reflectivity = 0.2)
        Cube(t, m)
    }

    val legMaterial = Material(tableColor, ambient = 0.2, diffuse = 0.7)

    val leg1 = run {
        val t = Matrix.translate(2.7, 1.5, -1.7) * Matrix.scale(0.1, 1.5, 0.1)
        Cube(t, legMaterial)
    }

    val leg2 = run {
        val t = Matrix.translate(2.7, 1.5, 1.7) * Matrix.scale(0.1, 1.5, 0.1)
        Cube(t, legMaterial)
    }

    val leg3 = run {
        val t = Matrix.translate(-2.7, 1.5, -1.7) * Matrix.scale(0.1, 1.5, 0.1)
        Cube(t, legMaterial)
    }

    val leg4 = run {
        val t = Matrix.translate(-2.7, 1.5, 1.7) * Matrix.scale(0.1, 1.5, 0.1)
        Cube(t, legMaterial)
    }

    val glassCube = run {
        val t = Matrix.translate(0, 3.45001, 0) * Matrix.rotateY(0.2) *
                Matrix.scale(0.25, 0.25, 0.25)
        val m = Material(Color(1, 1, 0.8), ambient = 0.0, diffuse = 0.3, specular = 0.9,
            shininess = 300.0, reflectivity = 0.7, transparency = 0.7, refractiveIndex = 1.5)
        Cube(t, m, false)
    }

    val littleCube1 = run {
        val t = Matrix.translate(1, 3.35, -0.9) * Matrix.rotateY(-0.4) *
                Matrix.scale(0.15, 0.15, 0.15)
        val m = Material(Color(1, 0.5, 0.5), reflectivity = 0.6, diffuse = 0.4)
        Cube(t, m)
    }

    val littleCube2 = run {
        val t = Matrix.translate(-1.5, 3.27, 0.3) * Matrix.rotateY(0.4) *
                Matrix.scale(0.15, 0.07, 0.15)
        val m = Material(Color(1, 1, 0.5))
        Cube(t, m)
    }

    val littleCube3 = run {
        val t = Matrix.translate(0, 3.25, 1) * Matrix.rotateY(0.4) *
                Matrix.scale(0.2, 0.05, 0.05)
        val m = Material(Color(0.5, 1, 0.5))
        Cube(t, m)
    }

    val littleCube4 = run {
        val t = Matrix.translate(-0.6, 3.4, -1) * Matrix.rotateY(0.8) *
                Matrix.scale(0.05, 0.2, 0.05)
        val m = Material(Color(0.5, 0.5, 1))
        Cube(t, m)
    }

    val littleCube5 = run {
        val t = Matrix.translate(2, 3.4, 1) * Matrix.rotateY(0.8) *
                Matrix.scale(0.05, 0.2, 0.05)
        val m = Material(Color(0.5, 1, 1))
        Cube(t, m)
    }

    val frame1 = run {
        val t = Matrix.translate(-10, 4, 1) * Matrix.scale(0.05, 1, 1)
        val m = Material(Color(0.7098, 0.2471, 0.2196), diffuse = 0.6)
        Cube(t, m)
    }

    val frame2 = run {
        val t = Matrix.translate(-10, 3.4, 2.7) * Matrix.scale(0.05, 0.4, 0.4)
        val m = Material(Color(0.2667, 0.2706, 0.6902), diffuse = 0.6)
        Cube(t, m)
    }

    val frame3 = run {
        val t = Matrix.translate(-10, 4.6, 2.7) * Matrix.scale(0.05, 0.4, 0.4)
        val m = Material(Color(0.3098, 0.5961, 0.3098), diffuse = 0.6)
        Cube(t, m)
    }

    val frame = run {
        val t = Matrix.translate(-2, 3.5, 9.95) * Matrix.scale(5, 1.5, 0.05)
        val m = Material(Color(0.3882, 0.2627, 0.1882), diffuse = 0.7)
        Cube(t, m)
    }

    val mirror = run {
        val t = Matrix.translate(-2, 3.5, 9.95) * Matrix.scale(4.8, 1.4, 0.06)
        val m = Material(Color.BLACK, diffuse = 0.0, ambient = 0.0, specular = 0.0,
            shininess = 300.0, reflectivity = 1.0)
        Cube(t, m)
    }

    val world = run {
        val light = PointLight(Tuple.point(0, 6.9, -5), Color(1, 1, 0.9))
        World(listOf(floorCeiling, walls, tableTop, leg1, leg2, leg3, leg4, glassCube,
            littleCube1, littleCube2, littleCube3, littleCube4, littleCube5,
            frame1, frame2, frame3, frame, mirror), light)
    }

    val camera = run {
        val from = Tuple.point(8, 6, -8)
        val to = Tuple.point(0, 3, 0)
        val t = from.viewTransformationFrom(to, Tuple.VY)
        Camera(2400, 1200, 0.7805, t)
    }

    val canvas = camera.render(world)
    canvas.toPPMFile(File("output/ch12_world.ppm"))
}
