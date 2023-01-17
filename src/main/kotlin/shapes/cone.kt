package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.Intersection
import kotlin.math.sqrt

class Cone(minimum: Number = Double.NEGATIVE_INFINITY,
           maximum: Number = Double.POSITIVE_INFINITY,
           val closed: Boolean = false,
           transformation: Matrix = Matrix.I,
           material: Material = Material(),
           castsShadow: Boolean = true):
    Shape(transformation, material, castsShadow) {

    val minimum = minimum.toDouble()
    val maximum = maximum.toDouble()

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        val a = rayLocal.direction.x * rayLocal.direction.x -
                rayLocal.direction.y * rayLocal.direction.y +
                rayLocal.direction.z * rayLocal.direction.z
        val b = 2 * rayLocal.origin.x * rayLocal.direction.x -
                2 * rayLocal.origin.y * rayLocal.direction.y +
                2 * rayLocal.origin.z * rayLocal.direction.z
        val c = rayLocal.origin.x * rayLocal.origin.x -
                rayLocal.origin.y * rayLocal.origin.y +
                rayLocal.origin.z * rayLocal.origin.z

        // Only check for intersections with the body of the cylinder if a is not near 0.
        val xs1 = if (almostEquals(0.0, a)) {
            if (almostEquals(0.0, b)) emptyList()
            else listOf(Intersection(-c / (2 * b), this))
        }
        else {
            val disc = b * b - 4 * a * c

            if (disc < 0)
                emptyList<Intersection>()

            val sqrtDisc = sqrt(disc)
            val (t0, t1) = run {
                val t0Tmp = (-b - sqrtDisc) / (2 * a)
                val t1Tmp = (-b + sqrtDisc) / (2 * a)
                if (t0Tmp <= t1Tmp) Pair(t0Tmp, t1Tmp) else Pair(t1Tmp, t0Tmp)
            }

            // Check for intersections with the body of the cylinder.
            val y0 = rayLocal.origin.y + t0 * rayLocal.direction.y
            val y1 = rayLocal.origin.y + t1 * rayLocal.direction.y
            val t0Int = minimum < y0 && y0 < maximum
            val t1Int = minimum < y1 && y1 < maximum
            when (Pair(t0Int, t1Int)) {
                Pair(true, true) -> listOf(Intersection(t0, this), Intersection(t1, this))
                Pair(true, false) -> listOf(Intersection(t0, this))
                Pair(false, true) -> listOf(Intersection(t1, this))
                else -> emptyList()
            }
        }

        // Check for intersections with the caps of the cylinder, if appropriate.
        val xs2 = intersectCaps(rayLocal)

        return xs1 + xs2
    }

    // Helper function to check to see if intersection at t is within a radius of 1
    // (i.e. radius of cylinder) from the y-axis.
    private fun checkCap(ray: Ray, t: Double, y: Double): Boolean {
        val x = ray.origin.x + t * ray.direction.x
        val z = ray.origin.z + t * ray.direction.z
        return x * x + z * z <= y * y
    }

    // Return any intersections with the caps of a closed cylinder.
    private fun intersectCaps(ray: Ray): List<Intersection> {
        if (!closed or almostEquals(0, ray.direction.y))
            return emptyList()

        val tMin = (minimum - ray.origin.y) / ray.direction.y
        val tMax = (maximum - ray.origin.y) / ray.direction.y
        return when(Pair(checkCap(ray, tMin, minimum), checkCap(ray, tMax, maximum))) {
            Pair(true, true) -> listOf(Intersection(tMin, this), Intersection(tMax, this))
            Pair(true, false) -> listOf(Intersection(tMin, this))
            Pair(false, true) -> listOf(Intersection(tMax, this))
            else -> emptyList()
        }
    }

    override fun localNormalAt(localPoint: Tuple): Tuple {
        val dist = localPoint.x * localPoint.x + localPoint.z * localPoint.z

        // Check first if we are at the caps if they apply, and otherwise if we are on the body.
        return if (dist < 1.0 && localPoint.y >= maximum - DEFAULT_PRECISION)
            Tuple.VY
        else if (dist < 1.0 && localPoint.y <= minimum + DEFAULT_PRECISION)
            -Tuple.VY
        else {
            val y0 = sqrt(dist)
            val y = if (localPoint.y > 0) -y0 else y0
            return Tuple.vector(localPoint.x, y, localPoint.z)
        }
    }
}
