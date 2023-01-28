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
    private val optimization: Optimization = Optimization.OCTREE,
    parent: Shape? = null): Shape(transformation, material, castsShadow, parent) {

    // Make copies of all the children to backreference this as their parent.
    val children = run { children.map { it.withParent(this) } }
    val size = children.size
    val isEmpty = children.isEmpty()
    val isNotEmpty = children.isNotEmpty()

    private val isAllTriangles: Boolean by lazy {
        children.all { it is Triangle }
    }

    operator fun get(idx: Int): Shape =
        children[idx]

    operator fun contains(s: Shape): Boolean =
        s in children

    // Note due to Kotlin semantics, we have to use objMaterial here.
    override fun withParent(parent: Shape?): Shape =
        Group(children, transformation, objMaterial, castsShadow, optimization, parent)

    fun withTransformation(transformation: Matrix): Shape {
        if (!transformation.isTransformation())
            throw IllegalArgumentException("Shapes must have 4x4 transformation matrices:\n" +
                    "\tShape: ${javaClass.name}\nTransformation:\n${transformation.show()}")
        return Group(children, transformation, material, castsShadow, optimization, parent)
    }

    override fun withMaterial(material: Material): Shape =
        Group(children.map { it.withMaterial(material) }, transformation, material, castsShadow, optimization, parent)

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
    // We do not need to sort as World sorts all intersections by t.
    private fun localIntersectAll(rayLocal: Ray): List<Intersection> =
        if (bounds.intersects(rayLocal).isNotEmpty())
            children.flatMap { it.intersect(rayLocal) }
        else
            emptyList()

    override
    fun localIntersect(rayLocal: Ray): List<Intersection> = when (optimization) {
        Optimization.KDTREE -> kdTree?.localIntersect(rayLocal) ?: localIntersectAll(rayLocal)
        Optimization.OCTREE -> octTree?.localIntersect(rayLocal) ?: localIntersectAll(rayLocal)
        else -> localIntersectAll(rayLocal)
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple =
        throw NotImplementedError("Groups do not have local normals.")

    override val bounds: BoundingBox by lazy {
        // At first, the bounds are a completely empty box, with:
        // 1. minPoint at INF, INF, INF
        // 2. maxPoint at -INF, -INF, -INF.
        // We make the space larger from the children.
        children.boundingBox()
    }

    // If we are a triangle mesh, we can have a KD Tree.
    private val kdTree: KDTreeNode? by lazy {
        if (optimization == Optimization.KDTREE && isAllTriangles)
            buildKDTree(bounds, children.map { it as Triangle } )
        else null
    }

    // If we are a triangle mesh, we can have an OctTree.
    private val octTree: OctTreeNode? by lazy {
        if (optimization == Optimization.OCTREE)
            buildOctTree(OctTreeNode.cubify(bounds), children)
        else null
    }

    companion object {
        // Note that these only work for triangle meshes.
        enum class Optimization {
            NONE,
            KDTREE,
            OCTREE
        }
    }
}
