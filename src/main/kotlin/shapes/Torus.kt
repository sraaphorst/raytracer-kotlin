package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.BoundingBox
import math.Intersection

// A torus is a quartic surface, and thus requires the Durand-Kerner polynomial solver.
// The inner radius r is the radius from the center to where the torus actually begins.
// The outer radius R is the radius from the center to where the torus actually ends.
// Thus, if thought of in two dimensions, the torus would have area π * (R^2 - r^2).
class Torus(
    val innerRadius: Double,
    val outerRadius: Double,
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

        val c0 = e * e - fourA2 * (outer2 - oy2)
        val c1 = 4.0 * f * e + 2.0 * fourA2 * oy * dy
        val c2 = 2.0 * sumD2 * e + 4.0 * f * f + fourA2 * dy2
        val c3 = 4.0 * sumD2 * f
        val c4 = sumD2 * sumD2

        return durandKernerSolver(listOf(c0, c1, c2, c3, c4))
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
