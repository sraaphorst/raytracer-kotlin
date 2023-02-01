package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.BoundingBox
import math.Intersection

// Radii are equivalent to the two torus that we are planning on joining.
// innerRadius is R, outerRadius is r (radius of torus body)
// The shape we then want is fully defined by the torus radii.
//
// It comprises circles in the y-z plane with radius f(x) where x ∈ [-R, R] with radius f(x), such that:
// 1. f(0) = r (to meet the height of the two torus at their inmost intersection)
// 2. f(-R) = f(R) = 0 (to terminate where the two torus intersections terminate)
//
// We want to interpolate quadratically from -R to R with these conditions, which gives us a parabola
// segment defined by the equation:
//
// f(x) = -r x^2 / R^2 + r, x ∈ [-R, R]
//
// The shape we get then consists of the points:
//
// S = {(x,y,z) | y^2 + z^2 = f(x)^2, x ∈ [-R, R]}
//   = {(x,y,z) | y^2 + z^2 = r^2 (1 - x^2 / R^2), x ∈ [-R, R]}

class TorusIntersection(
    private val innerRadius: Double,
    private val outerRadius: Double,
    transformation: Matrix = Matrix.I,
    material: Material? = null,
    castsShadow: Boolean = true,
    parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent) {

    // For convenience in equations.
    private val r = outerRadius
    private val bigR = innerRadius
    private val bigR2 = bigR * bigR

    constructor(innerRadius: Number,
                outerRadius: Number,
                transformation: Matrix = Matrix.I,
                material: Material? = null,
                castsShadow: Boolean = true,
                parent: Shape? = null):
            this(innerRadius.toDouble(), outerRadius.toDouble(), transformation, material, castsShadow, parent)

    override fun withParent(parent: Shape?): Shape =
        TorusIntersection(innerRadius, outerRadius, transformation, material, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        TorusIntersection(innerRadius, outerRadius, transformation, material, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        // We are in the bounding box if we reach this point, so x ∈ [-R, R], y ∈ [-r, r], z ∈ [-r, r].
        val (ox, oy, oz) = rayLocal.origin
        val (dx, dy, dz) = rayLocal.direction
        val ox2 = ox * ox
        val dx2 = dx * dx

        // Calculate f(ox) and f(dx) as we will need them for a, b, and c.
        // f(x) = r * (1 - x^2 / R^2)
        val fox = r * (1 - ox2 / bigR2)
        val fdx = r * (1 - dx2 / bigR2)

        // These are based on the values of a, b, c for the cone.
        // TODO: Unsure if these are correct?
        val a = dy * dy + dz * dz - fdx * fdx
        val b = 2 * oy * dy + 2 * oz * dz - fox * fdx
        val c = oy * oy + oz * oz - fox * fox

        // Check the discriminant.
        val ts = processDiscriminant(a, b, c)
        return if (ts.isEmpty()) emptyList()
        else ts.map { Intersection(it, this) }
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        TODO("Not yet implemented")
    }

    override val bounds: BoundingBox by lazy {
        BoundingBox(
            Tuple.point(-bigR, -r, -r),
            Tuple.point(bigR, r, r)
        )
    }
}