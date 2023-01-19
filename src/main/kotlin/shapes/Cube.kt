package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.Intersection
import kotlin.math.absoluteValue

class Cube(transformation: Matrix = Matrix.I,
           material: Material = Material(),
           castsShadow: Boolean = true,
           parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent) {
    override fun withParent(parent: Shape?): Shape =
        Cube(transformation, material, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        Cube(transformation, material, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> =
        bounds.intersects(rayLocal).map { Intersection(it, this) }

    override fun localNormalAt(localPoint: Tuple): Tuple {
        val posX = localPoint.x.absoluteValue
        val posY = localPoint.y.absoluteValue
        val posZ = localPoint.z.absoluteValue
        return when(maxOf(posX, posY, posZ)) {
            posX -> Tuple.vector(localPoint.x, 0,0)
            posY -> Tuple.vector(0, localPoint.y, 0)
            else -> Tuple.vector(0, 0, localPoint.z)
        }
    }

    override val bounds: BoundingBox by lazy {
        BoundingBox(Tuple.point(-1, -1, -1), Tuple.point(1, 1, 1))
    }
}
