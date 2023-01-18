package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.Intersection
import math.Matrix
import math.Ray
import math.Tuple

class Group(transformation: Matrix = Matrix.I,
            children: List<Shape> = emptyList(),
            castsShadow: Boolean = true,
            parent: Shape? = null):
    Shape(transformation, Material(), castsShadow, parent) {

    // Make copies of all the children to backreference this as their parent.
    val children = children.map { it.withParent(this) }

    operator fun contains(s: Shape): Boolean =
        s in children

    override fun withParent(parent: Shape?): Shape =
        Group(transformation, children, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> =
        children.flatMap { it.intersect(rayLocal) }.sortedBy { it.t }

    override fun localNormalAt(localPoint: Tuple): Tuple {
        TODO("Not yet implemented")
    }
}
