package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.Intersection
import java.util.UUID
import kotlin.math.sqrt

abstract class Shape(val transformation: Matrix,
                     material: Material? = null,
                     val castsShadow: Boolean,
                     val parent: Shape?,
                     private val id: UUID = UUID.randomUUID()) {
    init {
        if (!transformation.isTransformation())
            throw IllegalArgumentException("Shapes must have 4x4 transformation matrices:\n" +
                    "\tShape: ${javaClass.name}\nTransformation:\n${transformation.show()}")
    }

    // We need to store the parameter passed in for material here in order to propagate it correctly.
    // We will access it below: if an object does not have a material, it will try to see if it has a parent
    // with a material.
    protected val objMaterial = material

    val material: Material
        get() = objMaterial ?: (parent?.objMaterial ?: DefaultMaterial)

    // This method should only be invoked by Groups containing the object.
    internal abstract fun withParent(parent: Shape? = null): Shape

    // This method creates a copy of the object with the specified material.
    abstract fun withMaterial(material: Material): Shape

    // deepContains performs the following:
    // 1. For basic Shapes, it checks if s is a reference to the shape.
    // 2. For Groups, it checks if s is the Group and if not, traverses the children.
    // 3. For CSGShapes, it checks if s is the CSGShape, and if not, traverses the left and right.
    internal open fun deepContains(s: Shape): Boolean =
        this === s

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
    // We pass the hit to normalAt because it is needed by SmoothTriangle to calculate the interpolated normal.
    // To make test cases pass, give a default value of DummyIntersection.
    internal fun normalAt(worldPoint: Tuple, hit: Intersection = Intersection.DummyIntersection): Tuple {
        if (!worldPoint.isPoint())
            throw IllegalArgumentException("Shape::normalAt requires a point: $worldPoint")

        // Convert to object space.
        val localPoint = worldToLocal(worldPoint)
        val localNormal = localNormalAt(localPoint, hit)

        // Convert back to world space.
        return normalToWorld(localNormal)
    }

    // Normal at a point in object (local) space.
    // Normal should be returned in local space, and normalAt handles transforming it back to world space.
    // We pas the hit because it is used in SmoothTriangles to calculate the interpolated normal.
    internal abstract fun localNormalAt(localPoint: Tuple, hit: Intersection = Intersection.DummyIntersection): Tuple

    // Untransformed bounds for each Shape type.
    internal abstract val bounds: BoundingBox

    internal val parentBounds: BoundingBox by lazy {
        bounds.transform(transformation)
    }

    companion object {
        private val DefaultMaterial = Material()

        // Given values for a, b, and c, return an ordered list of t-values if there is a (possible) intersection.
        internal fun processDiscriminant(a: Double, b: Double, c: Double): List<Double> {
            val disc = b * b - 4 * a * c
            if (disc < 0)
                return emptyList()

            val t0 = (-b - sqrt(disc)) / (2 * a)
            val t1 = (-b + sqrt(disc)) / (2 * a)
            return listOf(t0, t1).sorted()
        }
    }
}

// Find the bounding box around a collection of Shapes.
internal fun <T: Shape> Collection<T>.boundingBox(): BoundingBox =
    fold(BoundingBox.Empty) { curr, shape -> curr.merge(shape.parentBounds) }