package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import kotlin.math.absoluteValue

class Plane(transformation: Matrix = Matrix.I,
            material: Material = Material(),
            castsShadow: Boolean = true,
            parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent) {
    override fun withParent(parent: Shape?): Shape =
        Plane(transformation, material, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        Plane(transformation, material, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        if (rayLocal.direction.y.absoluteValue < DEFAULT_PRECISION)
            return emptyList()
        val t = -(rayLocal.origin.y) / rayLocal.direction.y
        return listOf(Intersection(t, this))
    }

    override fun localNormalAt(localPoint: Tuple): Tuple =
        Tuple.VY

    override val bounds: BoundingBox by lazy {
        BoundingBox(
            Tuple.point(Double.NEGATIVE_INFINITY, 0, Double.NEGATIVE_INFINITY),
            Tuple.point(Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY)
        )
    }
}
