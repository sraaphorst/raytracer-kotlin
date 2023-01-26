package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.Intersection
import kotlin.math.sqrt

class Sphere(transformation: Matrix = Matrix.I,
             material: Material? = null,
             castsShadow: Boolean = true,
             parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent) {

    // Note due to Kotlin semantics, we have to use objMaterial here.
    override fun withParent(parent: Shape?): Shape =
        Sphere(transformation, objMaterial, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
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

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple =
        localPoint - Tuple.PZERO

    override val bounds: BoundingBox by lazy {
        BoundingBox(Tuple.point(-1, -1, -1), Tuple.point(1, 1, 1))
    }

    companion object {
        internal fun glassSphere(transformation: Matrix = Matrix.I,
                        transparency: Double = 1.0,
                        refractiveIndex: Double = 1.5) = run {
            val m = Material(transparency = transparency, refractiveIndex = refractiveIndex)
            Sphere(transformation, m)
        }
    }
}
