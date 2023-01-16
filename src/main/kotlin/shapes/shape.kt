package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.Intersection
import math.Matrix
import math.Ray
import math.Tuple
import java.util.UUID

abstract class Shape(val transformation: Matrix,
                     val material: Material,
                     private val id: UUID = UUID.randomUUID()) {
    init {
        if (!transformation.isTransformation())
            throw IllegalArgumentException("Shapes must have 4x4 transformation matrices:\n" +
                    "\tShape: ${javaClass.name}\nTransformation:\n${transformation.show()}")
    }

    // Convert a point from world space to object space.
    // Later on, we use parent here.
    fun worldToLocal(tuple: Tuple): Tuple =
        transformation.inverse * tuple

    // Convert a normal vector from object space to world space.
    // Later on, we will use parent here.
    private fun normalToWorld(localNormal: Tuple): Tuple =
        (transformation.inverse.transpose * localNormal).toVector().normalized

    // The intersect method transforms the ray to object space and then passes
    // it to localNormalAt, which should comprise the concrete implementation of
    // calculating the intersections with the Shape.
    internal fun intersect(rayWorld: Ray): List<Intersection> =
        // Transform the ray into object space.
        localIntersect(rayWorld.transform(transformation.inverse))

    internal abstract fun localIntersect(rayLocal: Ray): List<Intersection>

    // normalAt transforms the point to object space and passes it to localNormalAt
    // which should comprise the concrete implementation of calculating the normal vector
    // at the point for the Shape. Then normalAt transforms it back into world space.
    internal fun normalAt(worldPoint: Tuple): Tuple {
        if (!worldPoint.isPoint())
            throw IllegalArgumentException("Shape::normalAt requires a point: $worldPoint")

        // Convert to object space.
        val localPoint = worldToLocal(worldPoint)
        val localNormal = localNormalAt(localPoint)

        // Convert back to world space.
        return normalToWorld(localNormal)
    }
    internal abstract fun localNormalAt(localPoint: Tuple): Tuple


    // We want shapes to be considered only equal if they represent exactly the same shape.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Shape) return false

        if (id != other.id) return false
        if (transformation != other.transformation) return false
        if (material != other.material) return false

        return true
    }

    override fun hashCode(): Int =
        31 * (31 * transformation.hashCode() + material.hashCode()) + id.hashCode()
}
