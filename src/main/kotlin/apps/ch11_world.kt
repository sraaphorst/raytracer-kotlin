package apps

// By Sebastian Raaphorst, 2023.
// From https://forum.raytracerchallenge.com/thread/4/reflection-refraction-scene-description

import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import pattern.CheckerPattern
import pattern.StripedPattern
import scene.Camera
import scene.World
import shapes.Plane
import shapes.Sphere
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val wallMaterial = run {
        val t = Matrix.rotateY(PI / 2) * Matrix.scale(0.25, 0.25, 0.25)
        val p = StripedPattern(Color(0.45, 0.45, 0.45), Color(0.55, 0.55, 0.55), t)
        Material(p, ambient = 0.0, diffuse = 0.4, specular = 0.0, reflectivity = 0.3)
    }

    val floorBase = run {
        val p = CheckerPattern(Color(0.35, 0.35, 0.35), Color(0.65, 0.65, 0.65))
        val m = Material(p, specular = 0.0, reflectivity = 0.4)
        Plane(Matrix.rotateY(PI / 10), m)
    }

    val ceiling = run {
        val m = Material(Color(0.8, 0.8, 0.8), ambient = 0.3, specular = 0.0)
        Plane(Matrix.translate(0, 5, 0), m)
    }

    val leftWall = run {
        val t = Matrix.translate(-5, 0, 0) * Matrix.rotateZ(PI / 2) * Matrix.rotateY(PI / 2)
        Plane(t, wallMaterial)
    }

    val rightWall = run {
        val t = Matrix.translate(5, 0, 0) * Matrix.rotateZ(PI / 2) * Matrix.rotateY(PI / 2)
        Plane(t, wallMaterial)
    }

    val frontWall = run {
        val t = Matrix.translate(0, 0, 5) * Matrix.rotateX(PI / 2)
        Plane(t, wallMaterial)
    }

    val backWall = run {
        val t = Matrix.translate(0, 0, -5) * Matrix.rotateX(PI / 2)
        Plane(t, wallMaterial)
    }

    val backSphere1 = run {
        val t = Matrix.translate(4.6, 0.4, 1.0) * Matrix.scale(0.4, 0.4, 0.4)
        val m = Material(Color(0.8, 0.5, 0.3), shininess = 50.0)
        Sphere(t, m)
    }

    val backSphere2 = run {
        val t = Matrix.translate(4.7, 0.3, 0.4) * Matrix.scale(0.3, 0.3, 0.3)
        val m = Material(Color(0.9, 0.4, 0.5), shininess = 50.0)
        Sphere(t, m)
    }

    val backSphere3 = run {
        val t = Matrix.translate(-1, 0.5, 4.5) * Matrix.scale(0.5, 0.5, 0.5)
        val m = Material(Color(0.4, 0.9, 0.6), shininess = 50.0)
        Sphere(t, m)
    }

    val backSphere4 = run {
        val t = Matrix.translate(-1.7, 0.3, 4.7) * Matrix.scale(0.3, 0.3, 0.3)
        val m = Material(Color(0.4, 0.6, 0.9), shininess = 50.0)
        Sphere(t, m)
    }

    val redSphere = run {
        val t = Matrix.translate(-0.6, 1, 0.6)
        val m = Material(Color(1, 0.3, 0.2), specular = 0.4, shininess = 5.0)
        Sphere(t, m)
    }

    val greenSphere = run {
        val t = Matrix.translate(-0.7, 0.5, -0.8) * Matrix.scale(0.5, 0.5, 0.5)
        val m = Material(Color(0, 0.2, 0), ambient = 0.0, diffuse = 0.4, specular = 0.9,
            shininess = 300.0, reflectivity = 0.9, transparency = 0.9, refractiveIndex = 1.5)
        Sphere(t, m)
    }

    val blueSphere = run {
        val t = Matrix.translate(0.6, 0.7, -0.6) * Matrix.scale(0.7, 0.7, 0.7)
        val m = Material(Color(0, 0, 0.2), ambient = 0.0, diffuse = 0.4, specular = 0.9,
            shininess = 300.0, reflectivity = 0.9, transparency = 0.9, refractiveIndex = 1.5)
        Sphere(t, m)
    }

    val world = run {
        val light = PointLight(Tuple.point(-4.9, 4.9, -1))
        World(listOf(floorBase, ceiling, leftWall, rightWall, backWall, frontWall,
        backSphere1, backSphere2, backSphere3, backSphere4, redSphere, greenSphere, blueSphere
        ), light)
    }

    val camera = run {
        val from = Tuple.point(-2.6, 1.5, -3.9)
        val to = Tuple.point(-0.6, 1, -0.8)
        val t = from.viewTransformationFrom(to, Tuple.VY)
        Camera(2400, 1200, 1.152, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/ch11_world.ppm"))
    }
    println("Time elapsed: ${elapsed / 1000.0} s")
}
