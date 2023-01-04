package shapes

// By Sebastian Raaphorst, 2023.

import math.Intersection
import math.Matrix
import math.Ray
import java.util.UUID

abstract class Shape(val transformation: Matrix,
                     private val id: UUID = UUID.randomUUID()) {
    init {
        if (transformation.m != 4 || transformation.n != 4)
            throw IllegalArgumentException("Shapes must have 4x4 transformation matrices:\n" +
                "\tShape: $javaClass\nTransformation:\n${transformation.show()}")
    }

    abstract fun intersect(ray: Ray): List<Intersection>

    // We want shapes to be considered only equal if they represent exactly the same shape.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Shape) return false

        if (id != other.id) return false
        if (transformation != other.transformation) return false

        return true
    }

    override fun hashCode(): Int =
        32 * (id.hashCode()) + transformation.hashCode()
}
