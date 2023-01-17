package scene

import light.Light
import light.PointLight
import material.Material
import math.*
import pattern.Pattern
import shapes.Plane
import shapes.Shape
import shapes.Sphere
import kotlin.math.sqrt

data class World(val shapes: List<Shape>, val lights: List<Light>) {
    constructor(shapes: List<Shape>, light: Light):
            this(shapes, listOf(light))

    operator fun contains(shape: Shape): Boolean =
        shape in shapes

    internal fun shadeHit(comps: Computations, remaining: Int = DEFAULT_REMAINING): Color =
        if (lights.isEmpty())
            Color.BLACK
        else {
            val surface =lights.map { light ->
                comps.shape.material.lighting(
                    comps.shape, light,
                    comps.overPoint, comps.eyeV, comps.normalV,
                    isShadowed(comps.overPoint, light)
                )
            }.reduce { c1, c2 -> c1 + c2 }
            val reflected = reflectedColor(comps, remaining)
            val refracted = refractedColor(comps, remaining)

            if (comps.shape.material.reflectivity > 0 && comps.shape.material.transparency > 0) {
                val reflectance = comps.schlick
                surface + reflected * reflectance + refracted * (1 - reflectance)
            } else
                surface + reflected + refracted
        }

    // Determine if a point is in shadow with respect to a light source.
    // Note this is different from the book since it takes the light source as a parameter
    // since we are supporting multiple light sources.
    internal fun isShadowed(point: Tuple, light: Light): Boolean {
        if (!point.isPoint())
            throw IllegalArgumentException("World::isShadowed expects point: $point")

        // Create a shadow ray from each point of intersection towards the light source.
        // If something intersects that sho ray, then the point is in shadow.
        val vector = light.position - point
        val distance = vector.magnitude
        val direction = vector.normalized

        val ray = Ray(point, direction)
        val xs = intersect(ray)
        val hit = xs.hit(true)

        return hit != null && hit.t < distance
    }

    // This should be the entry point into World.
    // It connects the other functions together, which would be private if not for test cases.
    internal fun colorAt(r: Ray, remaining: Int = DEFAULT_REMAINING): Color {
        val xs = intersect(r)
        return xs.hit()?.computations(r, xs)?.let { shadeHit(it, remaining) } ?: Color.BLACK
    }

    internal fun intersect(ray: Ray): List<Intersection> =
        shapes.flatMap { it.intersect(ray) }.sortedBy { it.t }

    // Calculate the reflected color in a Computations object.
    internal fun reflectedColor(comps: Computations, remaining: Int = DEFAULT_REMAINING): Color =
        if (remaining <= 0 || comps.shape.material.reflectivity == 0.0)
            Color.BLACK
        else {
            val reflectRay = Ray(comps.overPoint, comps.reflectV)
            comps.shape.material.reflectivity * colorAt(reflectRay, remaining - 1)
        }

    internal fun refractedColor(comps: Computations, remaining: Int = DEFAULT_REMAINING): Color =
        if (remaining == 0 || almostEquals(0.0, comps.shape.material.transparency))
            Color.BLACK
        else {
            // Find the ratio of first index of refraction to second.
            // Comes from inverted definition of Snell's Law: sin A_i / sin A_t = n_2 / n_1.
            val nRatio = comps.n1 / comps.n2

            // cos(A_i) is same as dot product of the two vectors:
            val cosI = comps.eyeV.dot(comps.normalV)

            // Find sin(A_t)^2 via trigonometric identity.
            val sin2T = nRatio * nRatio * (1 - cosI * cosI)

            if (sin2T > 1) Color.BLACK
            else {
                val cosT = sqrt(1.0 - sin2T)
                val direction = comps.normalV * (nRatio * cosI - cosT) - nRatio * comps.eyeV

                // Create refracted ray.
                val refractRay = Ray(comps.underPoint, direction)

                // Find color of refracted ray. Multiplly by transparency to account for opacity.
                comps.shape.material.transparency * colorAt(refractRay, remaining - 1)
            }
        }

    companion object {
        internal val DefaultWorld: World by lazy {
            val light = PointLight(Tuple.point(-10, 10, -10))
            val m1 = Material(Color(0.8, 1.0, 0.6), diffuse = 0.7, specular = 0.2)
            val s1 = Sphere(material = m1)
            val s2 = Sphere(Matrix.scale(0.5, 0.5, 0.5))
            return@lazy World(listOf(s1, s2), light)
        }

        const val DEFAULT_REMAINING = 5
    }
}
