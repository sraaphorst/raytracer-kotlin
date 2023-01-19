package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.BoundingBox.Companion.MaxPoint
import math.BoundingBox.Companion.MinPoint
import math.Intersection

class Group(transformation: Matrix = Matrix.I,
            children: List<Shape> = emptyList(),
            castsShadow: Boolean = true,
            parent: Shape? = null):
    Shape(transformation, Material(), castsShadow, parent) {

    // Make copies of all the children to backreference this as their parent.
    val children = children.map { it.withParent(this) }
    val size = children.size
    val isEmpty = children.isEmpty()
    val isNotEmpty = children.isNotEmpty()

    operator fun get(idx: Int): Shape =
        children[idx]

    operator fun contains(s: Shape): Boolean =
        s in children

    override fun withParent(parent: Shape?): Shape =
        Group(transformation, children, castsShadow, parent)

    fun withTransformation(transformation: Matrix): Shape {
        if (!transformation.isTransformation())
            throw IllegalArgumentException("Shapes must have 4x4 transformation matrices:\n" +
                    "\tShape: ${javaClass.name}\nTransformation:\n${transformation.show()}")
        return Group(transformation, children, castsShadow, parent)
    }

    override fun withMaterial(material: Material): Shape =
        Group(transformation, children.map { it.withMaterial(material) }, castsShadow, parent)

    // Only process children if the ray intersects the bounding box for this group.
    override fun localIntersect(rayLocal: Ray): List<Intersection> =
        if (bounds.intersects(rayLocal).isNotEmpty())
            children.flatMap { it.intersect(rayLocal) }.sortedBy { it.t }
        else
            emptyList()

    override fun localNormalAt(localPoint: Tuple): Tuple =
        throw NotImplementedError("Groups do not have local normals.")

    override val bounds: BoundingBox by lazy {
        children.fold(BoundingBox(MaxPoint, MinPoint)) { curr, shape ->
            curr.merge(shape.parentBounds)
        }
    }
}
