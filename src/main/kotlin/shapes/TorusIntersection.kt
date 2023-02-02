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
// S = {(x,y,z) | y^2 + z^2 - f(x)^2 = 0, x ∈ [-R, R]}
//   = {(x,y,z) | y^2 + z^2 - (r^2 (1 - x^2 / R^2))^2, x ∈ [-R, R]}

class TorusIntersection(
    private val innerRadius: Double = 0.75,
    private val outerRadius: Double = 0.25,
    transformation: Matrix = Matrix.I,
    material: Material? = null,
    castsShadow: Boolean = true,
    parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent) {

    // For convenience in equations.
    private val r = outerRadius
    private val r2 = r * r
    private val bigR = innerRadius
    private val bigR2 = bigR * bigR
    private val bigR4 = bigR2 * bigR2

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
        val ox3 = ox2 * ox
        val ox4 = ox2 * ox2
        val dx2 = dx * dx
        val dx3 = dx2 * dx
        val dx4 = dx2 * dx2

        val oy2 = oy * oy
        val dy2 = dy * dy
        val oz2 = oz * oz
        val dz2 = dz * dz

//        val coefficients = listOf(
//            oy2 + oz2 - r2 * (ox4 - 2 * bigR2 * ox2 + bigR4),
//            2 * oy * dy + 2 * oz * dz - 4 * r2 * (ox3 * dx - bigR2 * ox * dx),
//            dy2 + dz2 - 2 * r2 * (3 * ox2 * dx2 - bigR2 * dx2) / bigR4,
//            -4 * r2 * ox * dx3 / bigR4,
//            -r2 * dx4 / bigR4
//        )

        val coefficients = listOf(
            oy2 + oz2 - r2 * (ox4 - 2 * ox2 * bigR2 + bigR4) / bigR4,
            2 * (oy * dy + oz * dz) - 4 * r2 * ox * dx * (ox2 - bigR2) / bigR4,
            dy2 + dz2 - 2 * r2 * dx2 * (3 * ox2 - bigR2) / bigR4,
            -4 * r2 * ox * dx3 / bigR4,
            -r2 * dx4 / bigR4
        )

//        val coefficients = listOf(
//            oy2 + oz2 - r2 * ox4 / bigR4 + 2 * r2 * ox2 / bigR2 - r2,
//            oy * dy + oz * dz - 4 * r2 * ox3 * dx / bigR4 + 2 * ox * dx / bigR2,
//            dy2 + dz2 - 6 * r2 * ox2 * dx2 / bigR4 + 2 * dx2 / bigR2,
//            -4 * r2 * ox * dx3 / bigR4,
//            -r2 * dx4 / bigR4
//        )

//        val coefficients = listOf(
//            oy2 + oz2 - r2 * ox4 / bigR4 + 2 * r2 * ox2 / bigR2 - r2,
//            2 * oy * dy + 2* oz * dz - r2 * 4 * ox3 * dx / bigR4 + 2 * r2 * ox * dx / bigR2,
//            dz2 + dz2 - r2 * 6 * ox2 * dx2 / bigR4 + r2 * dx2 / bigR2,
//            -r2 * 4 * ox * dx3 / bigR4,
//            -r2 * dx4 / bigR4
//        )

        println("\nCalling DK on $coefficients...")
        val sols = durandKernerSolver(coefficients)
        println("\nSolutions ${sols.size} are $sols.")
        val dSols = sols.filter { it.isReal && it.re.isFinite() }.map { it.re }
        val points = dSols.map { t -> Tuple.point(ox + t * dx, oy + t * dy, oz + t * dz) }
        println("Solutions ${points.size} in R: $points.")
        val evaluatedSolutions = points.map { (x, y, z) ->
            val e = (-r * x * x / bigR2 + r)
            Tuple.point(x, y, z)to (y * y + z * z - e * e)
        }
        val (goodSols, badSols) = evaluatedSolutions.partition { (_, ev) ->
            almostEquals(0.0,  ev)
        }
        println("Good solutions: $goodSols, Bad solutions: $badSols")

        return durandKernerSolver(coefficients)
            .filter { it.isReal && it.re.isFinite() }
            .map { Intersection(it.re, this) }
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        // The partial derivatives of: y^2 + z^2 - f(x)^2 = 0 are:
        // x' = d/dx -(-rx^2/R^2 + r)^2
        //    = - d/dx (-rx^2/R^2 + r)^2
        //    = -2(-rx^2/R^2 + r)(-2rx/R^2)
        //    = 4rx(-rx^2/R^2 + r)/R2
        // y' = 2y
        // z' = 2z, and x' = -4r^2 ( 1 - x^2 / R^2) (x / R^2)
        val (x, y, z) = localPoint
        val dx = 4 * r * x * (-r * x * x / bigR2 + r) / bigR2
        val dy = 2 * y
        val dz = 2 * z
        println("Normal at $localPoint is ($dx, $dy, $dz).")
        return Tuple.vector(dx, dy, dz)
    }

    override val bounds: BoundingBox by lazy {
        BoundingBox(
            Tuple.point(-bigR, -r, -r),
            Tuple.point(bigR, r, r)
        )
    }
}