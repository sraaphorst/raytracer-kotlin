package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.Intersection


sealed class Triangle(
    internal val p1: Tuple,
    internal val p2: Tuple,
    internal val p3: Tuple,
    transformation: Matrix = Matrix.I,
    material: Material? = null,
    castsShadow: Boolean = true,
    parent: Shape? = null
): Shape(transformation, material, castsShadow, parent) {
    init {
        if (!p1.isPoint() || !p2.isPoint() || !p3.isPoint())
            throw IllegalArgumentException("Triangle must be specified as three points: $p1, $p2, $p3.")
    }

    // Calculate the edge vectors.
    internal val e1 = p2 - p1
    internal val e2 = p3 - p1

    internal abstract fun createIntersection(t: Double, uv: Pair<Double, Double>? = null): Intersection

    internal fun createIntersection(t: Number, uv: Pair<Number, Number>? = null): Intersection =
        createIntersection(t.toDouble(), uv?.let { Pair(uv.first.toDouble(), uv.second.toDouble()) })

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        val dirCrossE2 = rayLocal.direction.cross(e2)
        val det = e1.dot(dirCrossE2)
        if (almostEquals(0, det))
            return emptyList()

        val f = 1.0 / det

        val p1ToOrigin = rayLocal.origin - p1
        val u = f * p1ToOrigin.dot(dirCrossE2)
        if (u < 0 || u > 1)
            return emptyList()

        val originCrossE1 = p1ToOrigin.cross(e1)
        val v = f * rayLocal.direction.dot(originCrossE1)
        if (v < 0 || u + v > 1)
            return emptyList()

        val t = f * e2.dot(originCrossE1)
        return listOf(createIntersection(t, Pair(u, v)))
    }

    // Center of the triangle: just the average of the three points.
    // Since we are adding three points and dividing by 3, the last coordinate should be 1.
    internal val center: Tuple by lazy {
        (p1 + p2 + p3) / 3
    }

    override val bounds: BoundingBox by lazy {
        val xMin = minOf(p1.x, p2.x, p3.x)
        val yMin = minOf(p1.y, p2.y, p3.y)
        val zMin = minOf(p1.z, p2.z, p3.z)
        val xMax = maxOf(p1.x, p2.x, p3.x)
        val yMax = maxOf(p1.y, p2.y, p3.y)
        val zMax = maxOf(p1.z, p2.z, p3.z)
        BoundingBox(Tuple.point(xMin, yMin, zMin), Tuple.point(xMax, yMax, zMax))
    }
}
