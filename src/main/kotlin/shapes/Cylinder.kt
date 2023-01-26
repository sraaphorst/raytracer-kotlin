package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.Intersection

class Cylinder(minimum: Number = Double.NEGATIVE_INFINITY,
               maximum: Number = Double.POSITIVE_INFINITY,
               closed: Boolean = false,
               transformation: Matrix = Matrix.I,
               material: Material? = null,
               castsShadow: Boolean = true,
               parent: Shape? = null):
    Cappable(minimum.toDouble(), maximum.toDouble(), closed, transformation, material, castsShadow, parent) {

    // Note due to Kotlin semantics, we have to use objMaterial here.
    override fun withParent(parent: Shape?): Shape =
        Cylinder(minimum, maximum, closed, transformation, objMaterial, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        Cylinder(minimum, maximum, closed, transformation, material, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        val a = rayLocal.direction.x * rayLocal.direction.x + rayLocal.direction.z * rayLocal.direction.z

        // Only check for intersections with the body of the cylinder if a is not near 0.
        val xs1 = if (almostEquals(0.0, a)) emptyList()
        else {
            val b = 2 * rayLocal.origin.x * rayLocal.direction.x + 2 * rayLocal.origin.z * rayLocal.direction.z
            val c = rayLocal.origin.x * rayLocal.origin.x + rayLocal.origin.z * rayLocal.origin.z - 1
            intersectBody(rayLocal, a, b, c)
        }

        // Check for intersections with the caps of the cylinder, if appropriate.
        val xs2 = intersectCaps(rayLocal, 1.0, 1.0)

        return xs1 + xs2
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        val dist = localPoint.x * localPoint.x + localPoint.z * localPoint.z

        // Check first if we are at the caps if they apply, and otherwise if we are on the body.
        return if (dist < 1.0 && localPoint.y >= maximum - DEFAULT_PRECISION)
            Tuple.VY
        else if (dist < 1.0 && localPoint.y <= minimum + DEFAULT_PRECISION)
            -Tuple.VY
        else
            Tuple.vector(localPoint.x, 0, localPoint.z)
    }

    override val bounds: BoundingBox by lazy {
        BoundingBox(Tuple.point(-1, minimum, -1), Tuple.point(1, maximum, 1))
    }
}
