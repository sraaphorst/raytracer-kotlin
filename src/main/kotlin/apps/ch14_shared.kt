package apps

// By Sebastian Raaphorst, 2023.

import math.Matrix
import shapes.Cylinder
import shapes.Group
import shapes.Sphere
import kotlin.math.PI

// The shared side of a hexagon.
val side = run {
    val corner = Sphere(Matrix.translate(0, 0, -1) * Matrix.scale(0.25, 0.25, 0.25))
    val edge = Cylinder(
        0, 1, false,
        Matrix.translate(0, 0, -1) * Matrix.rotateY(-PI / 6)
                * Matrix.rotateZ(-PI / 2) * Matrix.scale(0.25, 1, 0.25)
    )
    Group(listOf(corner, edge))
}