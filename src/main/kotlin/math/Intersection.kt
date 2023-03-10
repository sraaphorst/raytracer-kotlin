package math

// By Sebastian Raaphorst, 2023.

import shapes.Shape
import shapes.Sphere

// Note that u and v are only used with SmoothTriangle.
// They represent a location on a triangle relative to its corners.
internal data class Intersection(
    val t: Double,
    val shape: Shape,
    val uv: Pair<Double, Double>? = null) {
    constructor(t: Number, shape: Shape, uv: Pair<Number, Number>? = null):
            this(t.toDouble(), shape,
                uv?.let { Pair(uv.first.toDouble(), uv.second.toDouble()) } )

    init {
        if (uv != null && (uv.first < 0 || uv.first > 1 || uv.second < 0 || uv.second > 1))
            throw IllegalArgumentException("Illegal u/v value for smooth triangle: $shape has u=${uv}.")
    }

    // The hit is this, as calculated in World::colorAt, which is the only function that calls this.
    fun computations(ray: Ray, xs: List<Intersection> = listOf(this)): Computations {
        // The values returned correspond to n1 and n2.
        tailrec fun calculateNs(xsRemain: List<Intersection> = xs,
                                containers: List<Shape> = emptyList(),
                                ns: Pair<Double, Double> = Pair(0.0, 0.0)): Pair<Double, Double> {
            // If no intersections left, return the ns pair.
            if (xsRemain.isEmpty())
                return ns

            else {
                // Original values of n1, n2 at this point in the recursion.
                val (n10, n20) = ns
                val x = xsRemain.first()

                // Process n1.
                val n1 = run {
                    if (x === this)
                        if (containers.isEmpty()) 1.0 else containers.last().material.refractiveIndex
                    else n10
                }

                val nContainers = run {
                    // If a container is not in the list, we are encountering it for the first time, so enter.
                    // If it is already in the list, we are leaving it, so remove.
                    if (x.shape !in containers) containers + x.shape else containers - x.shape
                }

                // Process n2 and decide whether to continue or terminate.
                // If we have reached the hit, we have to terminate.
                return if (x === this) {
                    val n2 = if (nContainers.isEmpty()) 1.0 else nContainers.last().material.refractiveIndex

                    // We terminate the algorithm by returning the n-values.
                    Pair(n1, n2)
                } else
                    // Continue to loop over the unprocessed intersections with the new values.
                    calculateNs(xsRemain.drop(1), nContainers, Pair(n1, n20))
            }
        }

        val point = ray.position(t)
        val eyeV = -ray.direction
        val normalV = shape.normalAt(point, this)
        val inside = normalV.dot(eyeV) < 0
        val adjNormalV = if (inside) -normalV else normalV
        val reflectV = ray.direction.reflect(adjNormalV)
        val (n1, n2) = calculateNs()
        return Computations(t, shape, point, eyeV, adjNormalV, reflectV, inside, n1, n2)
    }

//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is Intersection) return false
//
//        if (!almostEquals(t, other.t)) return false
//        if (shape != other.shape) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = t.hashCode()
//        result = 31 * result + shape.hashCode()
//        return result
//    }
    companion object {
        // This is to simplify calls to localNormalAt in test cases.
        // They accept the DummyIntersection for the hit parameter since they do not rely on its value unless
        // they are a SmoothTriangle.
        internal val DummyIntersection = Intersection(0, Sphere())
    }
}

// The hit is the intersection with the smallest positive t value.
// In order to ignore objects that do not cast a shadow when looking for shadow hits,
// the function accepts a boolean parameter to indicate whether we are looking for a
// shadow hit: if so, shapes that do not cast a shadow are ignored.
internal fun List<Intersection>.hit(shadow: Boolean = false): Intersection? =
    this.filter { it.t >= 0 && (!shadow || it.shape.castsShadow) }.minByOrNull { it.t }
