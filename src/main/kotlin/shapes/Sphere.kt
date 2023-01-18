package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.Intersection
import math.Matrix
import math.Ray
import math.Tuple
import kotlin.math.sqrt

class Sphere(transformation: Matrix = Matrix.I,
             material: Material = Material(),
             castsShadow: Boolean = true,
             parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent) {
    override fun withParent(parent: Shape?): Shape =
        Sphere(transformation, material, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        val sphereToRay = rayLocal.origin - Tuple.PZERO

        val a = rayLocal.direction.dot(rayLocal.direction)
        val b = 2 * rayLocal.direction.dot(sphereToRay)
        val c = sphereToRay.dot(sphereToRay) - 1

        val discriminant = b * b - 4 * a * c
        return if (discriminant < 0)
            emptyList()
        else {
            val discriminantSqrt = sqrt(discriminant)
            val i1 = Intersection((-b - discriminantSqrt) / (2 * a), this)
            val i2 = Intersection((-b + discriminantSqrt) / (2 * a), this)
            return listOf(i1, i2)
        }
    }

    override fun localNormalAt(localPoint: Tuple): Tuple =
        localPoint - Tuple.PZERO

    companion object {
        internal fun glassSphere(transformation: Matrix = Matrix.I,
                        transparency: Double = 1.0,
                        refractiveIndex: Double = 1.5) = run {
            val m = Material(transparency = transparency, refractiveIndex = refractiveIndex)
            Sphere(transformation, m)
        }
    }
}
