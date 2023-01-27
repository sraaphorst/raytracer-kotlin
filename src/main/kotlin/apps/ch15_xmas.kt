package apps

// By Sebastian Raaphorst, 2023.
// From https://forum.raytracerchallenge.com/thread/16/merry-christmas-scene-description

// This example is particularly interesting since it contains groups containing groups that
// have all triangular elements: thus, the renderer will use bounding boxes for the mixed
// groups, but KDTrees for the subgroups containing only triangles.

import light.PointLight
import material.Material
import math.Color
import math.Matrix
import math.Tuple
import pattern.CheckerPattern
import scene.Camera
import scene.World
import shapes.*
import java.io.File
import kotlin.math.PI
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
// Characteristics of a branch.
    val branchLength = 2.0
    val branchRadius = 0.025
    val branchSegments = 20
    val needlesPerSegment = 24
    val segmentSize = branchLength / (branchSegments - 1)

    // Radial distance, in radians, between adjacent needles in a group.
    val theta = 2.1 * PI / needlesPerSegment

    // Maximum length of each needle.
    val maxNeedleLength = 20.0 * branchRadius

    val branchTransform = Matrix.scale(branchRadius, 1, branchRadius)
    val branchMaterial = Material(Color(0.5, 0.35, 0.26), ambient = 0.2, specular = 0, diffuse = 0.6)

    val needleMaterial = Material(Color(0.26, 0.36, 0.16), specular = 0.1)

    fun firBranch(): Group {
        // Group consisting of the needles on this branch.
        val needles = run {
            val segmentGroup = (0 until branchSegments).flatMap { segmentIdx ->
                (0 until needlesPerSegment).map { needleIdx ->
                    // Each needle is a triangle with yBase the y coordinate of the triangle's base.
                    val yBase = segmentSize * (segmentIdx + Random.nextDouble())

                    // yTip is the y coordinate of the tip of the triangle.
                    val yTip = yBase - Random.nextDouble() * segmentSize

                    // yAngle is the angle (radians) that the needle should be rotated around the branch.
                    val yAngle = theta * (needleIdx + Random.nextDouble())

                    // Determine length of the needle.
                    val needleLength = maxNeedleLength / 2 * (1 + Random.nextDouble())

                    // Amount needle is offset from the centre of the branch.
                    val needleOffset = branchRadius / 2

                    // Calculate the points of the needle.
                    val p1 = Tuple.point(needleOffset, yBase, needleOffset)
                    val p2 = Tuple.point(-needleOffset, yBase, needleOffset)
                    val p3 = Tuple.point(0, yTip, needleLength)

                    val t = Matrix.rotateY(yAngle)
                    PlainTriangle(p1, p2, p3, t, needleMaterial)
                }
            }
            Group(segmentGroup)
        }

        // Branch is just a closed cylinder.
        val branch = Cylinder(0, branchLength, true, branchTransform, branchMaterial)

        return Group(listOf(branch, needles))
    }

    val world = run {
        // Light sources are all coupled with physical objects, so that they appear as reflections on ornaments.
        val light1 = PointLight(Tuple.point(-10, 10, -10), Color(0.6, 0.6, 0.6))
        val sphere1 = Sphere(
             Matrix.translate(-10, 10, -10) * Matrix.scale(1.5, 1.5, 1.5),
            Material(Color.WHITE, ambient = 0.6, diffuse = 0, specular = 0),
            false
        )

        val light2 = PointLight(Tuple.point(10, 10, -10), Color(0.6, 0.6, 0.6))
        val sphere2 = Sphere(
            Matrix.translate(10, 10, -10) * Matrix.scale(1.5, 1.5, 1.5),
            Material(Color.WHITE, ambient = 0.6, diffuse = 0, specular = 0),
            false
        )

        val light3 = PointLight(Tuple.point(-2, 1, -6), Color(0.2, 0.1, 0.1))
        val sphere3 = Sphere(
            Matrix.translate(-2, 1, -6) * Matrix.scale(0.4, 0.4, 0.4),
            Material(Color(1, 0.5, 0.5), ambient = 0.6, diffuse = 0, specular = 0),
            false
        )

        val light4 = PointLight(Tuple.point(-1, -2, -6), Color(0.1, 0.2, 0.1))
        val sphere4 = Sphere(
            Matrix.translate(-1, -2, -6) * Matrix.scale(0.4, 0.4, 0.4),
                    Material(Color(0.5, 1,0.5), ambient = 0.6, diffuse = 0, specular = 0),
            false
        )

        val light5 = PointLight(Tuple.point(3, -1, -6), Color(0.2, 0.2, 0.2))
        val sphere5 = Sphere(
            Matrix.translate(3, -1, -6) * Matrix.scale(0.5, 0.5, 0.5),
                    Material(Color.WHITE, ambient = 0.6, diffuse = 0, specular = 0),
            false
        )

        val sphereGroup = Group(listOf(sphere1, sphere2, sphere3, sphere4, sphere5))
        val lights = listOf(light1, light2, light3, light4, light5)

        // The actual ornament. Note specular = 0 as we're making the ornament reflective and then putting
        // each light source inside another sphere, so that they show up as reflections.
        // The specular component of Phone shading simulates this sort of reflection, so we do not need it here.
        val ornamentGroup = run {
            val ornament = Sphere(
                Matrix.I,
                Material(Color(1, 0.25, 0.25), ambient = 0, specular = 0, diffuse = 0.5, reflectivity = 0.5)
            )

            // Silver crown atop the ornament.
            val crown = Cylinder(0.0, 1.0,
                transformation = Matrix.rotateZ(-0.1) * Matrix.translate(0, 0.9, 0) *
                        Matrix.scale(0.2, 0.3, 0.2),
                material = Material(
                    CheckerPattern(Color.WHITE, Color(0.94, 0.94, 0.94), Matrix.scale(0.2, 0.2, 0.2)),
                    ambient = 0.02, diffuse = 0.7, specular = 0.8, shininess = 20, reflectivity = 0.05
                )
            )

            Group(listOf(ornament, crown))
        }

        val branchGroup = run {
            val t = Matrix.rotateX(- PI / 2) * Matrix.translate(0, -0.5, 0)

            val branches = listOf(
                Matrix.translate(-1, -1, 0) * Matrix.rotateY(PI / 9),
                Matrix.translate(-1, 1, 0) * Matrix.rotateY(PI / 9),
                Matrix.translate(1, -1, 0) * Matrix.rotateY(-PI / 18),
                Matrix.translate(1, 1, 0) * Matrix.rotateY(-PI / 9),
                Matrix.translate(0.2, -1.25, 0) * Matrix.rotateY(-PI / 9),
                Matrix.translate(-0.2, -1.25, 0) * Matrix.rotateY(PI / 9),
                Matrix.translate(-1.2, 0.1, 0) * Matrix.rotateY(PI / 6) *
                        Matrix.rotateX(PI / 36),
                Matrix.translate(-1.2, -0.35, 0.5) * Matrix.rotateY(PI / 6) *
                        Matrix.rotateX(-PI / 18),
                Matrix.translate(-0.2, 1.5, 0.25) * Matrix.rotateY(-PI / 6) *
                        Matrix.rotateX(PI / 36),
                Matrix.translate(1.3, 0.4, 0) * Matrix.rotateY(-PI / 6) *
                        Matrix.rotateX(-PI / 36),
                Matrix.translate(1.5, -0.4, 0) * Matrix.rotateY(-PI / 18) *
                        Matrix.rotateX(PI / 36)
            ).map {
                firBranch().withTransformation(it * t)
            }

            Group(branches)
        }

        World(listOf(sphereGroup, ornamentGroup, branchGroup), lights)
    }

    val camera = run {
        val from = Tuple.point(0, 0, -4)
        val to = Tuple.PZERO
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        Camera(1600, 1200, PI / 3, t)
    }

    val elapsed = measureTimeMillis {
        val canvas = camera.render(world)
        canvas.toPPMFile(File("output/ch15_xmas.ppm"))
    }
    println("Time elapsed (rendering): ${elapsed / 1000.0} s")
}