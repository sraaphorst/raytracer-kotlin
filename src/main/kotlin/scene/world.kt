package scene

import light.Light
import light.PointLight
import material.Material
import math.*
import shapes.Shape
import shapes.Sphere

data class World(val shapes: List<Shape>, val lights: List<Light>) {
    constructor(shapes: List<Shape>, light: Light):
            this(shapes, listOf(light))

    operator fun contains(shape: Shape): Boolean =
        shape in shapes

    fun shadeHit(comps: Computations): Color =
        if (lights.isEmpty())
            Color.BLACK
        else
            lights.map { light ->
                comps.shape.material.lighting(light,
                    comps.overPoint, comps.eyeV, comps.normalV,
                    isShadowed(comps.overPoint, light))
            }.reduce { c1, c2 -> c1 + c2}

    // Determine if a point is in shadow with respect to a light source.
    // Note this is different from the book since it takes the light source as a parameter
    // since we are supporting multiple light sources.
    fun isShadowed(point: Tuple, light: Light): Boolean {
        if (!point.isPoint())
            throw IllegalArgumentException("World::isShadowed expects point: $point")

        // Create a shadow ray from each point of intersection towards the light source.
        // If something intersects that sho ray, then the point is in shadow.
        val vector = light.position - point
        val distance = vector.magnitude
        val direction = vector.normalized

        val ray = Ray(point, direction)
        val xs = intersect(ray)
        val hit = xs.hit()

        return hit != null && hit.t < distance
    }

    // This should be the entry point into World.
    // It connects the other functions together, which would be private if not for test cases.
    fun colorAt(r: Ray): Color =
        intersect(r).hit()?.computations(r)?.let { shadeHit(it) } ?: Color.BLACK

    fun intersect(ray: Ray): List<Intersection> =
        shapes.flatMap { it.intersect(ray) }.sortedBy { it.t }

    companion object {
        val DefaultWorld: World by lazy {
            val light1 = PointLight(Tuple.point(-10, 10, -10))
            val m1 = Material(Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)
            val s1 = Sphere(material = m1)
            val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5))
            return@lazy World(listOf(s1, s2), light1)
        }
    }
}
