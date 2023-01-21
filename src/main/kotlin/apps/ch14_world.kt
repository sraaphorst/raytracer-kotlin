package apps

// By Sebastian Raaphorst, 2023.
// From https://forum.raytracerchallenge.com/thread/13/groups-scene-description

import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import scene.Camera
import scene.World
import shapes.*
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val transforms = listOf(0, PI / 3, 2 * PI / 3, PI, 4 * PI / 3, 5 * PI / 3)
        .map(Matrix::rotateY)

    val leg = run {
        val sphere = Sphere(Matrix.translate(0, 0, -1) * Matrix.scale(0.25, 0.25, 0.25))
        val cylinder = Cylinder(0, 1, false,
            Matrix.translate(0, 0, -1) * Matrix.rotateY(-PI / 6) *
            Matrix.rotateZ(-PI / 2) * Matrix.scale(0.25, 1, 0.25))
        Group(children = listOf(sphere, cylinder))
    }

    val cap = run {
        val trans = Matrix.rotateX(-PI / 4 ) * Matrix.scale(0.24606, 1.37002, 0.24606)
        val cones = transforms.map {
            Cone(-1, 0, false, it * trans)
        }
        Group(children = cones)
    }

    val wacky = run {
        val legs = transforms.map(leg::withTransformation)
        val cap1 = cap.withTransformation(Matrix.translate(0, 1, 0))
        val cap2 = cap.withTransformation(Matrix.rotateX(PI) * Matrix.translate(0, 1, 0))
        Group(children = legs + listOf(cap1, cap2))
    }

    val backdrop = run {
        val t = Matrix.translate(0, 0, 100) * Matrix.rotateX(PI / 2)
        val m = Material(Color.WHITE, ambient = 1, diffuse = 0, specular = 0)
        Plane(t, m)
    }

    val wacky1 = run {
        val t = Matrix.translate(-2.8, 0, 0) * Matrix.rotateX(0.4363) * Matrix.rotateY(PI / 18)
        val m = Material(Color(0.9, 0.2, 0.4), ambient = 0.2, diffuse = 0.8, specular = 0.7, shininess = 20)
        wacky.withTransformation(t).withMaterial(m)
    }

    val wacky2 = run {
        val t = Matrix.rotateY(PI / 18)
        val m = Material(Color(0.2, 0.9, 0.6), ambient = 0.2, diffuse = 0.8, specular = 0.7, shininess = 20)
        wacky.withTransformation(t).withMaterial(m)
    }

    val wacky3 = run {
        val t = Matrix.translate(2.8, 0, 0) * Matrix.rotateX(-0.4363) * Matrix.rotateY(-PI / 18)
        val m = Material(Color(0.2, 0.3, 1), ambient = 0.2, diffuse = 0.8, specular = 0.7, shininess = 20)
        wacky.withTransformation(t).withMaterial(m)
    }

    val world = run {
        val color = Color(0.25, 0.25, 0.25)
        val light1 = PointLight(Tuple.point(10_000, 10_000, -10_000), color)
        val light2 = PointLight(Tuple.point(-10_000, 10_000, -10_000), color)
        val light3 = PointLight(Tuple.point(10_000, -10_000, -10_000), color)
        val light4 = PointLight(Tuple.point(-10_000, -10_000, -10_000), color)
        World(listOf(backdrop, wacky1, wacky2, wacky3), listOf(light1, light2, light3, light4))
    }

    val camera = run {
        val from = Tuple.point(0, 0, -9)
        val to = Tuple.PZERO
        val t = from.viewTransformationFrom(to, Tuple.VY)
        Camera(1200, 400, 0.9, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/ch14_world.ppm"))
    }
    println("Time elapsed: ${elapsed / 1000.0} s")
}

