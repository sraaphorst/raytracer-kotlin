package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.BoundingBox
import math.Intersection
import kotlin.math.absoluteValue

// The term "radius" here is misleading, but it is based on the concept of a torus.
// The outerRadius is half the length of the object (also called R).
// The innerRadius is the maximum radius of the object at its endpoint (also called r).
//
// It comprises circles in the y-z plane with radius f(x) where x ∈ [-R, R] with radius f(x), such that:
// 1. f(0) = r
// 2. f(-R) = f(R) = 0
//
// We want to interpolate quadratically from -R to R with these conditions, which gives us a parabola
// segment defined by the equation:
//
// f(x) = -r x^2 / R^2 + r, x ∈ [-R, R]
//
// The shape we get then consists of the points:
//
// S = {(x,y,z) | y^2 + z^2 - f(x)^2 = 0, x ∈ [-R, R]}
//   = {(x,y,z) | y^2 + z^2 - (r^2 (1 - x^2 / R^2))^2, x ∈ [-R, R]}
//
// We allow for the radius to extend past the outerRadius with a leftBoundary and a rightBoundary.

class Fusiform(
    private val outerRadius: Double = 0.75,
    private val innerRadius: Double = 0.25,
    private val leftBoundary: Double = -outerRadius,
    private val rightBoundary: Double = outerRadius,
    transformation: Matrix = Matrix.I,
    material: Material? = null,
    castsShadow: Boolean = true,
    parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent) {

    // For convenience in equations.
    private val r = innerRadius
    private val r2 = r * r
    private val bigR = outerRadius
    private val bigR2 = bigR * bigR
    private val bigR4 = bigR2 * bigR2

    override fun withParent(parent: Shape?): Shape =
        Fusiform(outerRadius, innerRadius, leftBoundary, rightBoundary, transformation, material, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        Fusiform(outerRadius, innerRadius, leftBoundary, rightBoundary, transformation, material, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        // We are in the bounding box if we reach this point, so x ∈ [-R, R], y ∈ [-r, r], z ∈ [-r, r].
        // Ray is parameterized for t:
        // ray = p + t * v
        // We solve for t to get the intersections by plugging values:
        // x = p.x + t * v.x
        // y = p.y + t * v.y
        // z = p.z + t * v.z
        // into the equation for the shape and then see if we can solve for t, which should have at
        // most two real solutions despite being a quartic polynomial.
        val (ox, oy, oz) = rayLocal.origin
        val (dx, dy, dz) = rayLocal.direction
        val ox2 = ox * ox
        val ox4 = ox2 * ox2
        val dx2 = dx * dx
        val dx3 = dx2 * dx
        val dx4 = dx2 * dx2

        val oy2 = oy * oy
        val dy2 = dy * dy
        val oz2 = oz * oz
        val dz2 = dz * dz

        val coefficients = listOf(
            oy2 + oz2 - r2 * (ox4 - 2 * ox2 * bigR2 + bigR4) / bigR4,
            2 * (oy * dy + oz * dz) - 4 * r2 * ox * dx * (ox2 - bigR2) / bigR4,
            dy2 + dz2 - 2 * r2 * dx2 * (3 * ox2 - bigR2) / bigR4,
            -4 * r2 * ox * dx3 / bigR4,
            -r2 * dx4 / bigR4
        )

        return durandKernerSolver(coefficients)
            .filter { it.isReal && it.re.isFinite() }
            .map { it.re }
            .filter {
                val x = ox + it * dx
                x in leftBoundary..rightBoundary
            }
            .map { Intersection(it, this) }
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        // The partial derivatives of: y^2 + z^2 - f(x)^2 = 0 are:
        // x' = d/dx -(-r x^2 / R^2 + r)^2
        //    = -d/dx (-r x^2 / R^2 + r)^2
        //    = -2(-r x^2 / R^2 + r)(-2 r x / R^2)
        //    = 4 r x (-r x^2 / R^2 + r) / R2
        //    = 4 r^2 x (R2 - x^2) / R4
        // y' = 2y
        // z' = 2z, and x' = -4r^2 ( 1 - x^2 / R^2) (x / R^2)
        val (x, y, z) = localPoint
        val dx  = 4 * r2 * x * (bigR2 - x * x) / bigR4
        val dy = 2 * y
        val dz = 2 * z
        return Tuple.vector(dx, dy, dz)
    }

    override val bounds: BoundingBox by lazy {
        BoundingBox(
            Tuple.point(leftBoundary, -r, -r),
            Tuple.point(rightBoundary, r, r)
        )
    }
}