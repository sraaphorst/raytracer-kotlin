package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.Intersection
import math.Matrix
import math.Ray
import math.Tuple
import kotlin.math.sqrt

class Sphere(transformation: Matrix = Matrix.I, material: Material = Material()): Shape(transformation, material) {
    override fun intersect(ray: Ray): List<Intersection> {
        val tRay = ray.transform(transformation.inverse)

        val sphereToRay = tRay.origin - Tuple.PZERO

        val a = tRay.direction.dot(tRay.direction)
        val b = 2 * tRay.direction.dot(sphereToRay)
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

    override fun normalAt(worldPoint: Tuple): Tuple {
        if (!worldPoint.isPoint())
            throw IllegalArgumentException("Sphere::normalAt cannot accept a vector: $worldPoint")
        val objectPoint = transformation.inverse * worldPoint
        val objectNormal = objectPoint - Tuple.PZERO

        // Convert back to a vector in world space.
        // Note w may have changed, so we have to transform it back.
        val worldNormal = transformation.inverse.transpose * objectNormal
        return worldNormal.toVector().normalized
    }
}
