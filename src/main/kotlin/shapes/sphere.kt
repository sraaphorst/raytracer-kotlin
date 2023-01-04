package shapes

// By Sebastian Raaphorst, 2023.

import math.Intersection
import math.Matrix
import math.Ray
import math.Tuple
import kotlin.math.sqrt

class Sphere(transformation: Matrix = Matrix.I): Shape(transformation) {
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
            val dsqrt = sqrt(discriminant)
            val i1 = Intersection((-b - dsqrt) / (2 * a), this)
            val i2 = Intersection((-b + dsqrt) / (2 * a), this)
            return listOf(i1, i2)
        }
    }
}
