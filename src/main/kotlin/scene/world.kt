package scene

import light.Light
import math.*
import shapes.Shape

data class World(val shapes: List<Shape>, val lights: List<Light>) {
    constructor(shapes: List<Shape>, light: Light):
            this(shapes, listOf(light))

    operator fun contains(shape: Shape): Boolean =
        shape in shapes

    fun shadeHit(comps: Computations): Color =
        if (lights.isEmpty())
            Color.BLACK
        else
            lights.map { comps.shape.material.lighting(it, comps.point, comps.eyeV, comps.normalV) }
                .reduce { c1, c2 -> c1 + c2}

    // This should be the entry point into World.
    // It connects the other functions together, which would be private if not for test cases.
    fun colorAt(r: Ray): Color =
        intersect(r).hit()?.computations(r)?.let { shadeHit(it) } ?: Color.BLACK

    fun intersect(ray: Ray): List<Intersection> =
        shapes.flatMap { it.intersect(ray) }.sortedBy { it.t }
}
