package org.vorpal.raytracer.shapes

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.material.Material
import org.vorpal.raytracer.math.BoundingBox
import org.vorpal.raytracer.math.DEFAULT_PRECISION
import org.vorpal.raytracer.math.Intersection
import org.vorpal.raytracer.math.Matrix
import org.vorpal.raytracer.math.Ray
import org.vorpal.raytracer.math.Tuple
import kotlin.math.absoluteValue

class Plane(transformation: Matrix = Matrix.I,
            material: Material? = null,
            castsShadow: Boolean = true,
            parent: Shape? = null):
    Shape(transformation, material, castsShadow, parent) {

    // Note due to Kotlin semantics, we have to use objMaterial here.
    override fun withParent(parent: Shape?): Shape =
        Plane(transformation, objMaterial, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        Plane(transformation, material, castsShadow, parent)

    override fun localIntersect(rayLocal: Ray): List<Intersection> {
        if (rayLocal.direction.y.absoluteValue < DEFAULT_PRECISION)
            return emptyList()
        val t = -(rayLocal.origin.y) / rayLocal.direction.y
        return listOf(Intersection(t, this))
    }

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple =
        Tuple.VY

    override val bounds: BoundingBox by lazy {
        BoundingBox(
            Tuple.point(Double.NEGATIVE_INFINITY, 0, Double.NEGATIVE_INFINITY),
            Tuple.point(Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY)
        )
    }
}
