package org.vorpal.raytracer.shapes

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.material.Material
import org.vorpal.raytracer.math.Intersection
import org.vorpal.raytracer.math.Matrix
import org.vorpal.raytracer.math.Tuple

class PlainTriangle(
    p1: Tuple,
    p2: Tuple,
    p3: Tuple,
    transformation: Matrix = Matrix.I,
    material: Material? = null,
    castsShadow: Boolean = true,
    parent: Shape? = null
): Triangle(p1, p2, p3, transformation, material, castsShadow, parent) {

    // Calculate the normal.
    internal val normal = e2.cross(e1).normalized

    override fun withParent(parent: Shape?): Shape =
        PlainTriangle(p1, p2, p3, transformation, material, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        PlainTriangle(p1, p2, p3, transformation, material, castsShadow, parent)

    // For plain triangles, we ignore u and v.
    override fun createIntersection(t: Double, uv: Pair<Double, Double>?): Intersection =
        Intersection(t, this)

    // Note that this will still return a normal if the point is not on the triangle.
    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple =
        normal
}
