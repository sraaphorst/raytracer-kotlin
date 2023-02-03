package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.BoundingBox
import math.Intersection

// TODO: Should be able to set a = b = c = 1 and scale to what we want with scaling of 1/a, 1/b, 1/c.
class Hyperboloid(
    private val a: Double,
    private val b: Double,
    private val c: Double,
    private val type: HyperboloidType,
    transformation: Matrix = Matrix.I,
    material: Material? = null,
    castsShadow: Boolean = true,
    parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent) {

        constructor(
            a: Number,
            b: Number,
            c: Number,
            type: HyperboloidType,
            transformation: Matrix = Matrix.I,
            material: Material? = null,
            castsShadow: Boolean = true,
            parent: Shape? = null
        ): this(a.toDouble(), b.toDouble(), c.toDouble(), type, transformation, material, castsShadow, parent)

    override fun withParent(parent: Shape?): Shape =
        Hyperboloid(a, b, c, type, transformation, material, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        Hyperboloid(a, b, c, type, transformation, material, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        val (ox, oy, oz) = rayLocal.origin
        val (dx, dy, dz) = rayLocal.direction

        // Since it is convention to use a, b, and c to define hyperboloids, we need other variable
        // names for the standard quadratic a, b, and c.
        val a2 = a * a
        val b2 = b * b
        val c2 = c * c

        val a0 = ox * ox * b2 * c2 + oy * oy * a2 * c2 - oz * oz * a2 * b2 - type.factor * a2 * b2 * c2
        val b0 = ox * dx * b2 * c2 + oy * dy * a2 * c2 - oz * dz * a2 * b2
        val c0 = dx * dx * b2 * c2 + dy * dy * a2 * c2 - dz * dz * a2 * b2

        val ts = processDiscriminant(a0, b0, c0)
        return ts.map { Intersection(it, this) }
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        val (x, y, z) = localPoint
        return Tuple.vector(2 * x / (a * a), 2 * y / (b * b), 2 * z / (c * c))
    }

    override val bounds: BoundingBox
        get() = TODO("Not yet implemented")

    companion object {
        enum class HyperboloidType(internal val factor: Double) {
            Hyperbolic(1.0),
            Elliptic(-1.0)
        }
    }
}