package apps

// By Sebastian Raaphorst, 2023.
// Cover image of The Ray Tracing Challenge, by Jamis Buck.

import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import scene.Camera
import scene.World
import shapes.Cube
import shapes.Plane
import shapes.Sphere
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val whiteMaterial = Material(Color.WHITE,
        diffuse = 0.7,
        ambient = 0.1,
        specular = 0.0,
        reflectivity = 0.1)

    val blueMaterial = Material(Color(0.537, 0.831, 0.914),
        diffuse = 0.7,
        ambient = 0.1,
        specular = 0.0,
        reflectivity = 0.1)

    val redMaterial = Material(Color(0.941, 0.322, 0.388),
        diffuse = 0.7,
        ambient = 0.1,
        specular = 0.0,
        reflectivity = 0.1)

    val purpleMaterial = Material(Color(0.373, 0.404, 0.550),
        diffuse = 0.7,
        ambient = 0.1,
        specular = 0.0,
        reflectivity = 0.1)

    val standardTransform = Matrix.scale(0.5, 0.5, 0.5) * Matrix.translate(1, -1, 1)
    val largeObject = Matrix.scale(3.5, 3.5, 3.5) * standardTransform
    val mediumObject = Matrix.scale(3, 3, 3) * standardTransform
    val smallObject = Matrix.scale(2, 2, 2) * standardTransform

    val plane = run {
        val t = Matrix.translate(0, 0, 500) * Matrix.rotateX(PI / 2)
        val m = Material(Color.WHITE, ambient = 1, diffuse = 0, specular = 0)
        Plane(t, m)
    }

    val sphere = run {
        val m = Material(Color(0.373, 0.404, 0.550),
            diffuse = 0.2,
            ambient = 0,
            specular = 1,
            shininess = 200,
            reflectivity = 0.7,
            transparency =  0.7,
            refractiveIndex =  1.5)
        Sphere(largeObject, m)
    }

    val cube1 = Cube(Matrix.translate(4, 0, 0) * mediumObject, whiteMaterial)
    val cube2 = Cube(Matrix.translate(8.5, 1.5, -0.5) * largeObject, blueMaterial)
    val cube3 = Cube(Matrix.translate(0, 0, 4) * largeObject, redMaterial)
    val cube4 = Cube(Matrix.translate(4, 0, 4) * smallObject, whiteMaterial)
    val cube5 = Cube(Matrix.translate(7.5, 0.5, 4) * mediumObject, purpleMaterial)
    val cube6 = Cube(Matrix.translate(-0.25, 0.25, 8) * mediumObject, whiteMaterial)
    val cube7 = Cube(Matrix.translate(4, 1, 7.5) * largeObject, blueMaterial)
    val cube8 = Cube(Matrix.translate(10, 2, 7.5) * mediumObject, redMaterial)
    val cube9 = Cube(Matrix.translate(8, 2, 12) * smallObject, whiteMaterial)
    val cube10 = Cube(Matrix.translate(20, 1, 9) * smallObject, whiteMaterial)
    val cube11 = Cube(Matrix.translate(-0.5, -5, 0.25) * largeObject, blueMaterial)
    val cube12 = Cube(Matrix.translate(4, -4, 0) * largeObject, redMaterial)
    val cube13 = Cube(Matrix.translate(8.5, -4, 0) * largeObject, whiteMaterial)
    val cube14 = Cube(Matrix.translate(0, -4, 4) * largeObject, whiteMaterial)
    val cube15 = Cube(Matrix.translate(-0.5, -4.5, 8) * largeObject, purpleMaterial)
    val cube16 = Cube(Matrix.translate(0, -8, 4) * largeObject, whiteMaterial)
    val cube17 = Cube(Matrix.translate(-0.5, -8.5, 8) * largeObject, whiteMaterial)

    val world = run {
        val shapes = listOf(plane, sphere, cube1, cube2, cube3, cube4, cube5, cube6, cube7, cube8, cube9,
            cube10, cube11, cube12, cube13, cube14, cube15, cube16, cube17)

        val light1 = PointLight(Tuple.point(50, 100, -50))
        val light2 = PointLight(Tuple.point(-400, 50, -10), Color(0.2, 0.2, 0.2))

        val lights = listOf(light1, light2)

        World(shapes, lights)
    }

    val camera = run {
        val from = Tuple.point(-6, 6, -10)
        val to = Tuple.point(6, 0, 6)
        val up = Tuple.vector(-0.45, 1, 0)
        val t = from.viewTransformationFrom(to, up)
        Camera(1000, 1000, PI / 4, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/cover.ppm"))
    }
    println("Time elapsed (rendering): ${elapsed / 1000.0} s")
}
