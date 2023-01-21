package apps

// By Sebastian Raaphorst, 2023.
// From https://forum.raytracerchallenge.com/thread/7/cylinders-scene-description

import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import pattern.CheckerPattern
import scene.Camera
import scene.World
import shapes.Cylinder
import shapes.Group
import shapes.Plane
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val flooring = run {
        val t = Matrix.rotateY(0.3) * Matrix.scale(0.25, 0.25, 0.25)
        val p = CheckerPattern(Color(0.5, 0.5, 0.5), Color(0.75, 0.75, 0.75), t)
        val m = Material(p, ambient = 0.2, diffuse = 0.9, specular = 0.0)
        Plane(Matrix.I, m)
    }

    val cylinder = run {
        val t = Matrix.translate(-1, 0, 1) * Matrix.scale(0.5, 1 ,0.5)
        val m = Material(Color(0, 0, 0.6), diffuse = 0.1,
            specular = 0.9, shininess = 300.0, reflectivity = 0.9)
        Cylinder(0, 0.75, true, t, m)
    }

    val concentricCylinders = run {
        val gt = Matrix.translate(1, 0, 0)
        val m = Material(Color(1, 1, 0.3), ambient = 0.1,
            diffuse = 0.8, specular = 0.9, shininess = 300.0)

        val concentricCylinder1 = run {
            val t = Matrix.scale(0.8, 1, 0.8)
            Cylinder(0, 0.2, false, t)
        }

        val concentricCylinder2 = run {
            val t = Matrix.scale(0.6, 1, 0.6)
            Cylinder(0, 0.3, false, t)
        }

        val concentricCylinder3 = run {
            val t = Matrix.scale(0.4, 1, 0.4)
            Cylinder(0, 0.4, false, t)
        }

        val concentricCylinder4 = run {
            val t = Matrix.scale(0.2, 1, 0.2)
            Cylinder(0, 0.5, true, t)
        }

        Group(gt, m, children=listOf(concentricCylinder1, concentricCylinder2, concentricCylinder3, concentricCylinder4))
    }

    val decorativeCylinder1 = run {
        val t = Matrix.translate(0, 0, -0.75) * Matrix.scale(0.05, 1, 0.05)
        val m = Material(Color.RED, ambient = 0.1, diffuse = 0.9, specular = 0.9, shininess = 300.0)
        Cylinder(0, 0.3, true, t, m)
    }

    val decorativeCylinder2 = run {
        val t = Matrix.translate(0, 0, -2.25) * Matrix.rotateY(-0.15) *
                Matrix.translate(0, 0, 1.5) * Matrix.scale(0.05, 1, 0.05)
        val m = Material(Color.YELLOW, ambient = 0.1, diffuse = 0.9, specular = 0.9, shininess = 300.0)
        Cylinder(0, 0.3, true, t, m)
    }

    val decorativeCylinder3 = run {
        val t = Matrix.translate(0, 0, -2.25) * Matrix.rotateY(-0.3) *
                Matrix.translate(0, 0, 1.5) * Matrix.scale(0.05, 1, 0.05)
        val m = Material(Color.GREEN, ambient = 0.1, diffuse = 0.9, specular = 0.9, shininess = 300.0)
        Cylinder(0, 0.3, true, t, m)
    }

    val decorativeCylinder4 = run {
        val t = Matrix.translate(0, 0, -2.25) * Matrix.rotateY(-0.45) *
                Matrix.translate(0, 0, 1.5) * Matrix.scale(0.05, 1, 0.05)
        val m = Material(Color.CYAN, ambient = 0.1, diffuse = 0.9, specular = 0.9, shininess = 300.0)
        Cylinder(0, 0.3, true, t, m)
    }

    val glassCylinder = run {
        val t = Matrix.translate(0, 0, -1.5) * Matrix.scale(0.33, 1, 0.33)
        val m = Material(Color(0.25, 0, 0), diffuse = 0.1, specular = 0.9,
            shininess = 300.0, reflectivity = 0.9, transparency = 0.9, refractiveIndex = 1.5)
        Cylinder(0.0001, 0.5, true, t, m)
    }

    val world = run {
        val light = PointLight(Tuple.point(1, 6.9, -4.9))
        World(listOf(flooring, cylinder,
            concentricCylinders,
            decorativeCylinder1, decorativeCylinder2, decorativeCylinder3, decorativeCylinder4,
            glassCylinder), light)
    }

    val camera = run {
        val from = Tuple.point(8, 3.5, -9)
        val to = Tuple.point(0, 0.3, 0)
        val t = from.viewTransformationFrom(to, Tuple.VY)
        Camera(2400, 1200, PI / 10, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/ch13_groups.ppm"))
    }
    println("Time elapsed: ${elapsed / 1000.0} s")
}
