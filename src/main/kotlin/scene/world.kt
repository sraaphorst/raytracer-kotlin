package scene

import light.Light
import math.Intersection
import math.Ray
import shapes.Shape

data class World(val shapes: Set<Shape>, val light: Light) {
    operator fun contains(shape: Shape): Boolean =
        shape in shapes

    fun intersect(ray: Ray): List<Intersection> =
        shapes.flatMap { it.intersect(ray) }.sortedBy { it.t }
}
