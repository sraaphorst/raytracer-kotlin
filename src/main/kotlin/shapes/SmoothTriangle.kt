package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.Intersection
import math.Matrix
import math.Tuple

class SmoothTriangle(
    p1: Tuple,
    p2: Tuple,
    p3: Tuple,
    internal val n1: Tuple,
    internal val n2: Tuple,
    internal val n3: Tuple,
    transformation: Matrix = Matrix.I,
    material: Material? = null,
    castsShadow: Boolean = true,
    parent: Shape? = null
): Triangle(p1, p2, p3, transformation, material, castsShadow, parent) {
    init {
        if (!n1.isVector() || !n2.isVector() || !n3.isVector())
            throw IllegalArgumentException("Smooth triangle requires three vector normals: $n1, $n2, $n3.")
    }

    override fun withParent(parent: Shape?): Shape =
        SmoothTriangle(p1, p2, p3, n1, n2, n3, transformation, material, castsShadow, parent)

    override fun withMaterial(material: Material): Shape =
        SmoothTriangle(p1, p2, p3, n1, n2, n3, transformation, material, castsShadow, parent)

    override fun localNormalAt(localPoint: Tuple, hit: Intersection): Tuple {
        val uv = hit.uv ?: throw IllegalArgumentException("SmoothTriangle received hit with u/v=null.")
        val (u, v) = uv
        return n2 * u + n3 * v + n1 * (1 - u - v)
    }

    override fun createIntersection(t: Double, uv: Pair<Double, Double>?): Intersection =
        Intersection(t, this, uv)
}
