package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.Intersection

class Triangle(
    internal val p1: Tuple,
    internal val p2: Tuple,
    internal val p3: Tuple,
    transformation: Matrix = Matrix.I,
    material: Material? = null,
    castsShadow: Boolean = true,
    parent: Shape? = null
): Shape(transformation, material, castsShadow, parent) {

    init {
        if (!p1.isPoint() || !p2.isPoint() || !p3.isPoint())
            throw IllegalArgumentException("Triangle must be specified as three points: $p1, $p2, $p3.")
    }

    // Calculate the edge vectors.
    internal val e1 = p2 - p1
    internal val e2 = p3 - p1
    internal val normal = e2.cross(e1).normalized

    override fun withParent(parent: Shape?): Shape =
        Triangle(p1, p2, p3, transformation, material, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        Triangle(p1, p2, p3, transformation, material, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        TODO("Not yet implemented")
    }

    // Note that this will still return a normal if the point is not on the triangle.
    override fun localNormalAt(localPoint: Tuple): Tuple =
        normal

    override val bounds: BoundingBox
        get() = TODO("Not yet implemented")
}