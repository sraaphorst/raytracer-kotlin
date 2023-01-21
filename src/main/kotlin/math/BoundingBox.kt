package math

// By Sebastian Raaphorst, 2023.

import kotlin.math.max
import kotlin.math.min

data class BoundingBox(val minPoint: Tuple = MaxPoint, val maxPoint: Tuple = MinPoint) {
    init {
        if (!minPoint.isPoint())
            throw IllegalArgumentException("BoundingBox minPoint is not a point: $minPoint.")
        if (!maxPoint.isPoint())
            throw IllegalArgumentException("BoundingBox maxPoint is not a point: $maxPoint.")
    }

    val isEmpty: Boolean by lazy {
        minPoint.x > maxPoint.x || minPoint.y > maxPoint.y || minPoint.z > maxPoint.z
    }
    val isNotEmpty: Boolean by lazy {
        !isEmpty
    }

    fun add(point: Tuple): BoundingBox {
        if (!point.isPoint())
            throw IllegalArgumentException("Tried to add vector to BoundingBox: $point.")
        return BoundingBox(
            Tuple.point(min(minPoint.x, point.x), min(minPoint.y, point.y), min(minPoint.z, point.z)),
            Tuple.point(max(maxPoint.x, point.x), min(maxPoint.y, point.y), min(maxPoint.z, point.z))
        )
    }

    fun merge(box: BoundingBox): BoundingBox =
        add(box.minPoint).add(box.maxPoint)

    fun transform(transformation: Matrix): BoundingBox {
        if (!transformation.isTransformation())
            throw IllegalArgumentException("Cannot transform a bounding box by a non-transform matrix.")
        val corners = listOf(
            minPoint,
            Tuple.point(maxPoint.x, minPoint.y, minPoint.z),
            Tuple.point(maxPoint.x, minPoint.y, maxPoint.z),
            Tuple.point(minPoint.x, minPoint.y, maxPoint.z),
            Tuple.point(minPoint.x, maxPoint.y, minPoint.z),
            Tuple.point(maxPoint.x, maxPoint.y, minPoint.z),
            maxPoint,
            Tuple.point(minPoint.x, maxPoint.y, maxPoint.z)
        )

        val transformedCorners = corners.map { transformation * it }

        // Find the new minPoint and maxPoint.
        val minX = transformedCorners.fold(Double.POSITIVE_INFINITY) { curr, p -> if (p.x < curr) p.x else curr }
        val minY = transformedCorners.fold(Double.POSITIVE_INFINITY) { curr, p -> if (p.y < curr) p.y else curr }
        val minZ = transformedCorners.fold(Double.POSITIVE_INFINITY) { curr, p -> if (p.z < curr) p.z else curr }
        val maxX = transformedCorners.fold(Double.NEGATIVE_INFINITY) { curr, p -> if (p.x > curr) p.x else curr }
        val maxY = transformedCorners.fold(Double.NEGATIVE_INFINITY) { curr, p -> if (p.y > curr) p.y else curr }
        val maxZ = transformedCorners.fold(Double.NEGATIVE_INFINITY) { curr, p -> if (p.z > curr) p.z else curr }

        return BoundingBox(Tuple.point(minX, minY, minZ), Tuple.point(maxX, maxY, maxZ))
    }

    // Check axis for AABB (axis aligned bounding box).
    // minimum and maximum specify the minimum and maximum values on the axis for this bounding box.
    private fun checkAxis(origin: Double, direction: Double, minimum: Double, maximum: Double): Pair<Double, Double> {
        val tMin = (minimum - origin) / direction
        val tMax = (maximum - origin) / direction
        return if (tMin > tMax) Pair(tMax, tMin) else Pair(tMin, tMax)
    }

    // Check if the ray intersects this bounding box, and if it does, return the t-values.
    internal fun intersects(rayLocal: Ray): List<Double> {
        val (xtMin, xtMax) = checkAxis(rayLocal.origin.x, rayLocal.direction.x, minPoint.x, maxPoint.x)
        val (ytMin, ytMax) = checkAxis(rayLocal.origin.y, rayLocal.direction.y, minPoint.y, maxPoint.y)
        val (ztMin, ztMax) = checkAxis(rayLocal.origin.z, rayLocal.direction.z, minPoint.z, maxPoint.z)

        val tMin = maxOf(xtMin, ytMin, ztMin)
        val tMax = minOf(xtMax, ytMax, ztMax)

        return if (tMin <= tMax)
            listOf(tMin, tMax)
        else
            emptyList()
    }
    operator fun contains(point: Tuple): Boolean {
        if (!point.isPoint())
            throw IllegalArgumentException("BoundingBox contains passed vector: $point.")
        return point.x >= minPoint.x && point.y >= minPoint.y && point.z >= minPoint.z &&
                point.x <= maxPoint.x && point.y <= maxPoint.y && point.z <= maxPoint.z
    }

    operator fun contains(box: BoundingBox): Boolean =
        contains(box.minPoint) && contains(box.maxPoint)

    companion object {
        internal val MaxPoint = Tuple.point(
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY
        )
        internal val MinPoint = Tuple.point(
            Double.NEGATIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.NEGATIVE_INFINITY
        )
    }
}
