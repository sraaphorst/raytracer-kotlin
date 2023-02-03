package apps

// By Sebastian Raaphorst, 2023.

import material.Material
import math.Color
import math.Matrix
import pattern.CheckerPattern
import shapes.Cube
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

val room = run {
    val t = Matrix.scale(6, 4, 8)
    val p = CheckerPattern(Color(0.9, 0.4, 0.4), Color.WHITE, Matrix.scale(0.5, 0.5, 0.5))
    val m = Material(p, specular = 0)
    Cube(t, m)
}