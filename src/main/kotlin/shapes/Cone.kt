package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.*
import math.Intersection
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.sqrt

class Cone(minimum: Number = Double.NEGATIVE_INFINITY,
           maximum: Number = Double.POSITIVE_INFINITY,
           closed: Boolean = false,
           transformation: Matrix = Matrix.I,
           material: Material? = null,
           castsShadow: Boolean = true,
           parent: Shape? = null):
    Cappable(minimum.toDouble(), maximum.toDouble(), closed, transformation, material, castsShadow, parent) {

    // Note due to Kotlin semantics, we have to use objMaterial here.
    override fun withParent(parent: Shape?): Shape =
        Cone(minimum, maximum, closed, transformation, objMaterial, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        Cone(minimum, maximum, closed, transformation, material, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        val a = rayLocal.direction.x * rayLocal.direction.x -
                rayLocal.direction.y * rayLocal.direction.y +
                rayLocal.direction.z * rayLocal.direction.z
        val b = 2 * rayLocal.origin.x * rayLocal.direction.x -
                2 * rayLocal.origin.y * rayLocal.direction.y +
                2 * rayLocal.origin.z * rayLocal.direction.z
        val c = rayLocal.origin.x * rayLocal.origin.x -
                rayLocal.origin.y * rayLocal.origin.y +
                rayLocal.origin.z * rayLocal.origin.z

        // Only check for intersections with the body of the cylinder if a is not near 0.
        val xs1 = if (almostEquals(0.0, a)) {
            if (almostEquals(0.0, b)) emptyList()
            else listOf(Intersection(-c / (2 * b), this))
        } else intersectBody(rayLocal, a, b, c)

        // Check for intersections with the caps of the cylinder, if appropriate.
        val xs2 = intersectCaps(rayLocal, minimum, maximum)

        return xs1 + xs2
    }

    override fun localNormalAt(localPoint: Tuple): Tuple {
        val dist = localPoint.x * localPoint.x + localPoint.z * localPoint.z

        // Check first if we are at the caps if they apply, and otherwise if we are on the body.
        return if (dist < 1.0 && localPoint.y >= maximum - DEFAULT_PRECISION)
            Tuple.VY
        else if (dist < 1.0 && localPoint.y <= minimum + DEFAULT_PRECISION)
            -Tuple.VY
        else {
            val y0 = sqrt(dist)
            val y = if (localPoint.y > 0) -y0 else y0
            return Tuple.vector(localPoint.x, y, localPoint.z)
        }
    }

    override val bounds: BoundingBox by lazy {
        val r = max(this.minimum.absoluteValue, this.maximum.absoluteValue)
        BoundingBox(Tuple.point(-r, minimum, -r), Tuple.point(r, maximum, r))
    }
}
