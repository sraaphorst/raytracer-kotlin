package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.almostEquals
import math.Intersection
import math.Matrix
import math.Ray
import kotlin.math.sqrt

// An object like a Cylinder or Cone that can be limited in length and cappable.
abstract class Cappable(
    internal val minimum: Double = Double.NEGATIVE_INFINITY,
    internal val maximum: Double = Double.POSITIVE_INFINITY,
    internal val closed: Boolean = false,
    transformation: Matrix = Matrix.I,
    material: Material? = null,
    castsShadow: Boolean = true,
    parent: Shape? = null
    ): Shape (transformation, material, castsShadow, parent) {

    private fun checkCap(rayLocal: Ray, radius: Double, t: Double): Boolean {
        val x = rayLocal.origin.x + t * rayLocal.direction.x
        val z = rayLocal.origin.z + t * rayLocal.direction.z
        return x * x + z * z <= radius * radius
    }

    internal fun intersectCaps(rayLocal: Ray, minRadius: Double, maxRadius: Double): List<Intersection> {
        if (!closed or almostEquals(0, rayLocal.direction.y))
            return emptyList()

        val tMin = (minimum - rayLocal.origin.y) / rayLocal.direction.y
        val tMax = (maximum - rayLocal.origin.y) / rayLocal.direction.y

        return when(Pair(checkCap(rayLocal, minRadius, tMin), checkCap(rayLocal, maxRadius, tMax))) {
            Pair(true, true) -> listOf(Intersection(tMin, this), Intersection(tMax, this))
            Pair(true, false) -> listOf(Intersection(tMin, this))
            Pair(false, true) -> listOf(Intersection(tMax, this))
            else -> emptyList()
        }
    }

    internal fun intersectBody(rayLocal: Ray, a: Double, b: Double, c: Double): List<Intersection> {
        val disc = b * b - 4 * a * c

        if (disc < 0)
            emptyList<Intersection>()

        val sqrtDisc = sqrt(disc)
        val (t0, t1) = run {
            val t0Tmp = (-b - sqrtDisc) / (2 * a)
            val t1Tmp = (-b + sqrtDisc) / (2 * a)
            if (t0Tmp <= t1Tmp) Pair(t0Tmp, t1Tmp) else Pair(t1Tmp, t0Tmp)
        }

        // Check for intersections with the body of the cone.
        val y0 = rayLocal.origin.y + t0 * rayLocal.direction.y
        val y1 = rayLocal.origin.y + t1 * rayLocal.direction.y
        val t0Int = minimum < y0 && y0 < maximum
        val t1Int = minimum < y1 && y1 < maximum
        return when (Pair(t0Int, t1Int)) {
            Pair(true, true) -> listOf(Intersection(t0, this), Intersection(t1, this))
            Pair(true, false) -> listOf(Intersection(t0, this))
            Pair(false, true) -> listOf(Intersection(t1, this))
            else -> emptyList()
        }
    }
}
