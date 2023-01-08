package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import kotlin.math.absoluteValue

class Plane(transformation: Matrix = Matrix.I, material: Material = Material()):
        Shape(transformation, material) {
    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        if (rayLocal.direction.y.absoluteValue < DEFAULT_PRECISION)
            return emptyList()
        val t = -(rayLocal.origin.y) / rayLocal.direction.y
        return listOf(Intersection(t, this))
    }

    override fun localNormalAt(localPoint: Tuple): Tuple =
        Tuple.VY
}
