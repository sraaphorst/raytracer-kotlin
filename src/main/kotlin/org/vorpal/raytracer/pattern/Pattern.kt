package org.vorpal.raytracer.pattern

// By Sebastian Raaphorst, 2023.

import org.vorpal.raytracer.math.Color
import org.vorpal.raytracer.math.Matrix
import org.vorpal.raytracer.math.Tuple
import org.vorpal.raytracer.shapes.Shape

abstract class Pattern(val transformation: Matrix = Matrix.I) {
    init {
        if (!transformation.isTransformation())
            throw IllegalArgumentException("${javaClass.name} requires a transformation:\n${transformation.show()}")
    }

    // Retrieves the color in world space.
    abstract fun patternAt(patternPoint: Tuple): Color

    // Retrieves color for given shape at world space point.
    fun colorAtShape(shape: Shape, worldPoint: Tuple): Color {
        val localPoint = shape.worldToLocal(worldPoint)
        val patternPoint = transformation.inverse * localPoint
        return patternAt(patternPoint)
    }
}
