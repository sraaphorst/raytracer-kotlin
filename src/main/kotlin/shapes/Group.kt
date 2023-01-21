package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.Intersection

class Group(
    children: List<Shape> = emptyList(),
    transformation: Matrix = Matrix.I,
    material: Material? = null,
    castsShadow: Boolean = true,
    parent: Shape? = null): Shape(transformation, material, castsShadow, parent) {

    // Make copies of all the children to backreference this as their parent.
    val children = run { children.map { it.withParent(this) } }
    val size = children.size
    val isEmpty = children.isEmpty()
    val isNotEmpty = children.isNotEmpty()

    operator fun get(idx: Int): Shape =
        children[idx]

    operator fun contains(s: Shape): Boolean =
        s in children

    // Note due to Kotlin semantics, we have to use objMaterial here.
    override fun withParent(parent: Shape?): Shape =
        Group(children, transformation, objMaterial, castsShadow, parent)

    fun withTransformation(transformation: Matrix): Shape {
        if (!transformation.isTransformation())
            throw IllegalArgumentException("Shapes must have 4x4 transformation matrices:\n" +
                    "\tShape: ${javaClass.name}\nTransformation:\n${transformation.show()}")
        return Group(children, transformation, material, castsShadow, parent)
    }

    override fun withMaterial(material: Material): Shape =
        Group(children.map { it.withMaterial(material) }, transformation, material, castsShadow, parent)

    fun forEach(f: (Shape) -> Unit) {
        children.forEach(f)
    }

    fun <T> map(f: (Shape) -> T): Iterable<T> =
        children.map(f)

    fun all(predicate: (Shape) -> Boolean): Boolean =
        children.all(predicate)

    fun none(predicate: (Shape) -> Boolean): Boolean =
        children.none(predicate)

    fun any(predicate: (Shape) -> Boolean): Boolean =
        children.any(predicate)

    fun <T> zip(other: Iterable<T>): Iterable<Pair<Shape, T>> =
        children.zip(other)

    fun zip(group: Group): Iterable<Pair<Shape, Shape>> =
        children.zip(group.children)

    fun withIndex(): Iterable<IndexedValue<Shape>> =
        children.withIndex()

    // Only process children if the ray intersects the bounding box for this group.
    override fun localIntersect(rayLocal: Ray): List<Intersection> =
        if (bounds.intersects(rayLocal).isNotEmpty())
            children.flatMap { it.intersect(rayLocal) }.sortedBy { it.t }
        else
            emptyList()

    override fun localNormalAt(localPoint: Tuple): Tuple =
        throw NotImplementedError("Groups do not have local normals.")

    override val bounds: BoundingBox by lazy {
        // At first, the bounds are a completely empty box, with:
        // 1. minPoint at INF, INF, INF
        // 2. maxPoint at -INF, -INF, -INF.
        // We make the space larger from the children.
        children.fold(BoundingBox.Empty) { curr, shape -> curr.merge(shape.parentBounds) }
    }
}
