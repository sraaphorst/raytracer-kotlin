package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.BoundingBox.Companion.MaxPoint
import math.BoundingBox.Companion.MinPoint
import math.Intersection
import kotlin.math.PI

class Group(transformation: Matrix = Matrix.I,
            material: Material = Material(),
            children: List<Shape> = emptyList(),
            castsShadow: Boolean = true,
            parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent) {

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
        Group(transformation, material, children, castsShadow, parent)

    fun withTransformation(transformation: Matrix): Shape {
        if (!transformation.isTransformation())
            throw IllegalArgumentException("Shapes must have 4x4 transformation matrices:\n" +
                    "\tShape: ${javaClass.name}\nTransformation:\n${transformation.show()}")
        return Group(transformation, material, children, castsShadow, parent)
    }

    fun forEach(f: (Shape) -> Unit): Unit {
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

    fun withIndex(group: Group): Iterable<IndexedValue<Shape>> =
        children.withIndex()

    override fun withMaterial(material: Material): Shape =
        Group(transformation, material, children.map { it.withMaterial(material) }, castsShadow, parent)

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


//fun main() {
//    val g1 = run {
//        val s1 = Sphere(transformation = Matrix.translate(-1, -1, -1) * Matrix.scale(0.5, 0.5, 0.5))
//        val c1 = Cylinder(transformation = Matrix.rotateY(PI / 2) * Matrix.scale(0.33, 0.33, 0.33))
//        Group(children = listOf(s1, c1))
//    }
//
//    // g1 is no longer relevant:
//    // 1. The children of g1g should have g1g as their parent.
//    // 2. The parent of g1g should be g2.
//    val g2 = Group(Matrix.scale(0.1, 0.1, 0.1), listOf(g1))
//    val g1g = g2.children[0] as Group
//
//    // 1. The children of g1p should have g1p as their parent.
//    // 2. The parent of g1p should be g2p.
//    val g2p = g2.withTransformation(Matrix.rotateX(PI))
//    val g1p = g2.children[0] as Group
//
//    // Parents
//     Check to see if g2's children have changed but maintain the same fundamental properties as g1's children.
//        g1.zip(g1p).forEach { (s1, s2) ->
//            assertNotSame(s1.parent, s2.parent)
//            assertSame(g1, s1.parent)
//            assertSame(g1p, s2.parent)
//        }
//        assertSame(g2, g1.parent)
//        assertSame(g2p, g1p.parent)
//    println("g2: $g2, g1.parent: ${g1.parent}")
//    println()
//}