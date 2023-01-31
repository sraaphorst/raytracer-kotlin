package apps

// By Sebastian Raaphorst, 2023.

import input.OBJParser
import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import scene.Camera
import scene.World
import shapes.Cube
import shapes.Cylinder
import shapes.Group
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val box = run {
        val t = Matrix.scale(0.268, 0.268, 0.268) * Matrix.translate(0, 0.1217, 0)
        val rawBox = Cube(
            Matrix.translate(-3.9863, -0.1217, -1.1820) *
                    Matrix.scale(3.73335, 2.5845, 1.6283) *
                    Matrix.translate(1, 1, 1),
            castsShadow = false
        )
        Group(listOf(rawBox), t)
    }

    val dragon = OBJParser.fromURL({}.javaClass.getResource("/dragon.obj")!!)
        .groups.getValue(OBJParser.DefaultGroup)
        .withTransformation(
            Matrix.scale(0.268, 0.268, 0.268) * Matrix.translate(0, 0.1217, 0)
        )

    val pedestal = run {
        val m = Material(Color(0.2, 0.2, 0.2), ambient = 0, diffuse = 0.8, specular = 0, reflectivity = 0.2)
        Cylinder(-0.15, 0, true, material = m)
    }

    val group1 = run {
        val t = Matrix.translate(0, 2, 0)

        val subgroup = run {
            val dm = Material(Color(1, 0, 0.1), ambient = 0.1, diffuse = 0.6, specular = 0.3, shininess = 15)
            val d = dragon.withMaterial(dm)

            val bm = Material(ambient = 0, diffuse = 0.4, specular = 0, transparency = 0.6, refractiveIndex = 1)
            val b = box.withMaterial(bm)

            Group(listOf(d, b))
        }

        Group(listOf(pedestal, subgroup), t)
    }

    val group2 = run {
        val t = Matrix.translate(2, 1, -1)

        val subgroup = run {
            val st = Matrix.scale(0.75, 0.75, 0.75) * Matrix.rotateY(4)

            val dm = Material(Color(1, 0.5, 0.1), ambient = 0.1, diffuse = 0.6, specular = 0.3, shininess = 15)
            val d = dragon.withMaterial(dm)

            val bm = Material(ambient = 0, diffuse = 0.2, specular = 0, transparency = 0.8, refractiveIndex = 1)
            val b = box.withMaterial(bm)

            Group(listOf(d, b), st)
        }

        Group(listOf(pedestal, subgroup), t)
    }

    val group3 = run {
        val t = Matrix.translate(-2, 0.75, -1)

        val subgroup = run {
            val st = Matrix.scale(0.75, 0.75, 0.75) * Matrix.rotateY(-0.4)

            val dm = Material(Color(0.9, 0.5, 0.1),
                ambient = 0.1, diffuse = 0.6, specular = 0.3, shininess = 15)
            val d = dragon.withMaterial(dm)

            val bm = Material(ambient = 0, diffuse = 0.2, specular = 0, transparency = 0.8, refractiveIndex = 1)
            val b = box.withMaterial(bm)

            Group(listOf(d, b), st)
        }

        Group(listOf(pedestal, subgroup), t)
    }

    val group4 = run {
        val t = Matrix.translate(-4, 0, -2)

        val subgroup = run {
            val st = Matrix.scale(0.5, 0.5, 0.5) * Matrix.rotateY(-0.2)

            val dm = Material(Color(1, 0.9, 0.1), ambient = 0.1, diffuse = 0.6, specular = 0.3, shininess = 15)
            val d = dragon.withMaterial(dm)

            val bm = Material(ambient = 0, diffuse = 0.1, specular = 0, transparency = 0.9, refractiveIndex = 1)
            val b = box.withMaterial(bm)

            Group(listOf(d, b), st)
        }

        Group(listOf(pedestal, subgroup), t)
    }

    val group5 = run {
        val t = Matrix.translate(4, 0, -2)

        val subgroup = run {
            val st = Matrix.scale(0.5, 0.5, 0.5) * Matrix.rotateY(3.3)

            val dm = Material(Color(0.9, 1, 0.1), ambient = 0.1, diffuse = 0.6, specular = 0.3, shininess = 15)
            val d = dragon.withMaterial(dm)

            val bm = Material(ambient = 0, diffuse = 0.1, specular = 0, transparency = 0.9, refractiveIndex = 1)
            val b = box.withMaterial(bm)

            Group(listOf(d, b), st)
        }

        Group(listOf(pedestal, subgroup), t)
    }

    val group6 = run {
        val t = Matrix.translate(0, 0.5, -4)

        val subgroup = run {
            val st = Matrix.rotateY(PI)
            val dm = Material(ambient = 0.1, diffuse = 0.6, specular = 0.3, shininess = 15)
            val d = dragon.withMaterial(dm)
            Group(listOf(d), st)
        }

        Group(listOf(pedestal, subgroup), t)
    }

    val world = run {
        val light1 = PointLight(Tuple.point(-10, 100, -100))
        val light2 = PointLight(Tuple.point(0, 100, 0), Color(0.1, 0.1, 0.1))
        val light3 = PointLight(Tuple.point(100, 10, -25), Color(0.2, 0.2, 0.2))
        val light4 = PointLight(Tuple.point(-100, 10, -25), Color(0.2, 0.2, 0.2))
        World(listOf(group1, group2, group3, group4, group5, group6),
              listOf(light1, light2, light3, light4))
    }

    val camera = run {
        val from = Tuple.point(0, 2.5, -10)
        val to = Tuple.PY
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        Camera(1500, 600, 1.2, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/ch16_dragons.ppm"))
    }
    println("Time elapsed (rendering): ${elapsed / 1000.0} s")
}
