package shapes

import material.Material
import math.Matrix
import java.awt.Point

class Triangle(
    transformation: Matrix = Matrix.I,
    material: Material? = null,
    castsShadow: Boolean = true,
    parent: Shape? = null,
    private val p1: Point,
    private val p2: Point,
    private val p3: Point) {

}