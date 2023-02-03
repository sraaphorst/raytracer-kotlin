package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.BoundingBox
import math.Intersection

// A torus is a quartic surface, and thus requires the Durand-Kerner polynomial solver.
// The inner radius r is the radius from the centre of the torus to the centre of the cylinder
//   making up the torus.
// The outer radius R is the radius of the cylinder making up the torus.
//
// Defaults of inner=0.75, outer=0.25 gives a torus of:
//   width  (x) -1 to 1
//   height (y) -0.25 to 0.25
//   depth  (z) -1 to 1
class Torus(
    private val innerRadius: Double = 0.75,
    private val outerRadius: Double = 0.25,
    transformation: Matrix = Matrix.I,
    material: Material? = null,
    castsShadow: Boolean = true,
    parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent) {

    constructor(innerRadius: Number,
                outerRadius: Number,
                transformation: Matrix = Matrix.I,
                material: Material? = null,
                castsShadow: Boolean = true,
                parent: Shape? = null):
                    this(innerRadius.toDouble(), outerRadius.toDouble(), transformation, material, castsShadow, parent)

    override fun withParent(parent: Shape?): Shape =
        Torus(innerRadius, outerRadius, transformation, material, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        Torus(innerRadius, outerRadius, transformation, material, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        if (bounds.intersects(rayLocal).isEmpty())
            return emptyList()

        val (ox, oy, oz) = rayLocal.origin
        val (dx, dy, dz) = rayLocal.direction
        val ox2 = ox * ox
        val oy2 = oy * oy
        val oz2 = oz * oz
        val dx2 = dx * dx
        val dy2 = dy * dy
        val dz2 = dz * dz

        val inner2 = innerRadius * innerRadius
        val outer2 = outerRadius * outerRadius

        val sumD2 = dx2 + dy2 + dz2
        val e = ox2 + oy2 + oz2 - inner2 - outer2
        val f = ox * dx + oy * dy + oz * dz
        val fourA2 = 4.0 * inner2

        val coefficients = listOf(
            e * e - fourA2 * (outer2 - oy2),
            4.0 * f * e + 2.0 * fourA2 * oy * dy,
            2.0 * sumD2 * e + 4.0 * f * f + fourA2 * dy2,
            4.0 * sumD2 * f,
            sumD2 * sumD2
        )

        return durandKernerSolver(coefficients)
            .filter { it.isReal && it.re.isFinite() }
            .map { Intersection(it.re, this) }
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        val innerSquared = innerRadius * innerRadius
        val pSquared = innerSquared + outerRadius * outerRadius
        val sSquared = localPoint.x * localPoint.x +
                localPoint.y * localPoint.y +
                localPoint.z * localPoint.z
        val diff = sSquared - pSquared

        return Tuple.vector(
            4 * localPoint.x * diff,
            4 * localPoint.y * (diff + 2 * innerSquared),
            4 * localPoint.z * diff
            )
    }

    override val bounds: BoundingBox =
        BoundingBox(
            Tuple.point(-(innerRadius + outerRadius), -outerRadius, -(innerRadius + outerRadius)),
            Tuple.point(innerRadius + outerRadius, outerRadius, innerRadius + outerRadius)
        )
}
