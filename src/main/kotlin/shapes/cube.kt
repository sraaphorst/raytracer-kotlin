package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.Intersection
import kotlin.math.absoluteValue

class Cube(transformation: Matrix = Matrix.I,
           material: Material = Material(),
           castsShadow: Boolean = true,
           parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent) {
    override fun withParent(parent: Shape?): Shape =
        Cube(transformation, material, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        val (xtMin, xtMax) = checkAxis(rayLocal.origin.x, rayLocal.direction.x)
        val (ytMin, ytMax) = checkAxis(rayLocal.origin.y, rayLocal.direction.y)
        val (ztMin, ztMax) = checkAxis(rayLocal.origin.z, rayLocal.direction.z)

        val tMin = maxOf(xtMin, ytMin, ztMin)
        val tMax = minOf(xtMax, ytMax, ztMax)

        return if (tMin <= tMax)
            listOf(Intersection(tMin, this), Intersection(tMax, this))
        else
            emptyList()
    }

    private fun checkAxis(origin: Double, direction: Double): Pair<Double, Double> {
        val tMin = (-1 - origin) / direction
        val tMax = (1 - origin) / direction
        return if (tMin > tMax) Pair(tMax, tMin) else Pair(tMin, tMax)
    }

    override fun localNormalAt(localPoint: Tuple): Tuple {
        val posX = localPoint.x.absoluteValue
        val posY = localPoint.y.absoluteValue
        val posZ = localPoint.z.absoluteValue
        return when(maxOf(posX, posY, posZ)) {
            posX -> Tuple.vector(localPoint.x, 0,0)
            posY -> Tuple.vector(0, localPoint.y, 0)
            else -> Tuple.vector(0, 0, localPoint.z)
        }
    }
}
