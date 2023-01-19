package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.Intersection
import java.util.UUID
import kotlin.math.PI

abstract class Shape(val transformation: Matrix,
                     val material: Material,
                     val castsShadow: Boolean,
                     val parent: Shape?,
                     private val id: UUID = UUID.randomUUID()) {
    init {
        if (!transformation.isTransformation())
            throw IllegalArgumentException("Shapes must have 4x4 transformation matrices:\n" +
                    "\tShape: ${javaClass.name}\nTransformation:\n${transformation.show()}")
    }

    // This method should only be invoked by Groups containing the object.
    internal abstract fun withParent(parent: Shape? = null): Shape

    // Convert a point from world space to object space.
    // Later on, we use parent here.
    internal fun worldToLocal(tuple: Tuple): Tuple =
        transformation.inverse * (parent?.worldToLocal(tuple) ?: tuple)

    // Convert a normal vector from object space to world space.
    // Later on, we will use parent here.
    internal fun normalToWorld(localNormal: Tuple): Tuple {
        val normal = (transformation.inverse.transpose * localNormal).toVector().normalized
        return parent?.normalToWorld(normal) ?: normal
    }

    // The intersect method transforms the ray to object space and then passes
    // it to localNormalAt, which should comprise the concrete implementation of
    // calculating the intersections with the Shape.
    internal fun intersect(rayWorld: Ray): List<Intersection> =
        // Transform the ray into object space.
        localIntersect(rayWorld.transform(transformation.inverse))

    internal abstract fun localIntersect(rayLocal: Ray): List<Intersection>

    // normalAt transforms the point from world to object (local) space and passes it to localNormalAt
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

    // Normal at a point in object (local) space.
    // Normal should be returned in local space, and normalAt handles transforming it back to world space.
    internal abstract fun localNormalAt(localPoint: Tuple): Tuple

    // Untransformed bounds for each Shape type.
    internal abstract val bounds: BoundingBox

    internal val parentBounds: BoundingBox by lazy {
        bounds.transform(transformation)
    }
}
