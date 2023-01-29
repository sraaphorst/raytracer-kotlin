package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*

// Operations we can perform on CSGShapes.
enum class Operation {
    Union {
        override fun intersectionAllowed(leftHit: Boolean, inLeft: Boolean, inRight: Boolean): Boolean =
            (leftHit && !inRight) || (!leftHit && !inLeft)
    },
    Intersection {
        override fun intersectionAllowed(leftHit: Boolean, inLeft: Boolean, inRight: Boolean): Boolean =
            (leftHit and inRight) || (!leftHit && inLeft)
    },
    Difference {
        override fun intersectionAllowed(leftHit: Boolean, inLeft: Boolean, inRight: Boolean): Boolean =
            (leftHit and !inRight) || (!leftHit && inLeft)
    };

    // If leftHit is false, it means the hit was from the right.
    internal abstract fun intersectionAllowed(leftHit: Boolean, inLeft: Boolean, inRight: Boolean): Boolean
}

// A Constructive Solid Geometry shape.
class CSGShape(
    val operation: Operation,
    left: Shape,
    right: Shape,
    transformation: Matrix = Matrix.I,
    material: Material = Material(),
    castsShadow: Boolean = true,
    parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent)

{
    val left = left.withParent(this)
    val right = right.withParent(this)

    override fun withParent(parent: Shape?): Shape =
        CSGShape(operation, left, right, transformation, material, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        CSGShape(operation, left, right, transformation, material, castsShadow, parent)

    override fun deepContains(s: Shape): Boolean = (this === s) || when (left) {
            is Group -> left.deepContains(s)
            is CSGShape -> left.deepContains(s) || right.deepContains(s)
            else -> left === s
        } || when (right) {
            is Group -> right.deepContains(s)
            is CSGShape -> right.deepContains(s) || right.deepContains(s)
            else -> right === s
        }

    // Take a list of intersections and determine which ones pass based on the intersections
    // allowed for the current operation.
    internal fun filterIntersection(xs: List<Intersection>): List<Intersection> =
        // Begin outside both children.
        // The parameters to fold are the list of intersections we are collection, inLeft, and inRight.
        // We begin outside of both children.
        xs.fold(Triple<List<Intersection>, Boolean, Boolean>(emptyList(), false, false)) {
            curr, x ->
                val (result, inLeft, inRight) = curr

                // If the intersection's shape is part of the left child, then leftHit is true.
                val leftHit = left.deepContains(x.shape)

                val newResult = if (operation.intersectionAllowed(leftHit, inLeft, inRight)) result + x else result
                val newInLeft = if (leftHit) !inLeft else inLeft
                val newInRight = if (leftHit) inRight else !inRight
                Triple(newResult, newInLeft, newInRight)
        }.first

    override fun localIntersect(rayLocal: Ray): List<Intersection> =
        if (bounds.intersects(rayLocal).isNotEmpty())
            filterIntersection((left.intersect(rayLocal) + right.intersect(rayLocal)).sortedBy { it.t })
        else
            emptyList()

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple =
        throw NotImplementedError("Normals are not defined for CSGShapes.")

    override val bounds: BoundingBox =
        left.parentBounds.merge(right.parentBounds)

    operator fun component1(): Shape =
        left

    operator fun component2(): Shape =
        right
}
