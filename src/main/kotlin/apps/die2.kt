package apps

// By Sebastian Raaphorst, 2023.

import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import pattern.CheckerPattern
import scene.Antialiasing
import scene.Camera
import scene.World
import shapes.Cube
import shapes.die6
import java.io.File
import kotlin.math.PI
import kotlin.system.measureTimeMillis

fun main() {
    val stage = run {
        val transform = Matrix.scale(10, 5, 8)
        val pattern = CheckerPattern(Color.fromHex(0x888888), Color.fromHex(0xAAAAAA),
            Matrix.scale(0.25, 0.25, 0.25))
        val material = Material(pattern, specular = 0)
        Cube(transform, material)
    }

    val pipMaterial = List(6) { Material(Color.WHITE) }

    val material1 = Material(Color.fromHex(0x1016AA), shininess = 50.0)
    val die1 = die6(faceMaterial = List(6) { material1 }, frameMaterial = material1, pipMaterial = pipMaterial)
        .withTransformation(Matrix.translate(-4, -4, 3) * Matrix.rotateY(PI / 6))

    val material2 = Material(Color.fromHex(0xA20412), shininess = 500.0)
    val die2 = die6(faceMaterial = List(6) { material2}, frameMaterial = material2, pipMaterial= pipMaterial)
        .withTransformation(Matrix.translate(0, -4, 2) * Matrix.rotateZ(PI) *
                Matrix.rotateY(PI/4.2))

    val material3 = Material(Color.fromHex(0x1AB422), shininess = 5.0)
    val die3 = die6(faceMaterial = List(6) { material3 }, frameMaterial = material3, pipMaterial = pipMaterial)
        .withTransformation(Matrix.translate(3.5, -4, 4) * Matrix.rotateZ(PI) *
            Matrix.rotateY(PI) * Matrix.rotateY(PI/5))

    val world = run {
        val light = PointLight(Tuple.point(1, 4, -5))
        World(listOf(stage, die1, die2, die3), listOf(light))
    }

    val camera = run {
        val from = Tuple.point(0, 1, -7)
        val to = Tuple.PZERO
        val t = from.viewTransformationFrom(to, Tuple.VY)
        Camera(1800, 1200, PI/2, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world, Antialiasing.BLUR)
        canvas.toPPMFile(File("output/die2.ppm"))
    }
    println("Time elapsed: ${elapsed / 1000.0} s")
}