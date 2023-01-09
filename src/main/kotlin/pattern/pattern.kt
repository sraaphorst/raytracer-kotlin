package pattern

// By Sebastian Raaphorst, 2023.

import math.Color
import math.Matrix
import math.Tuple
import shapes.Shape

abstract class Pattern(val transformation: Matrix) {
    init {
        if (!transformation.isTransformation())
            throw IllegalArgumentException("${javaClass.name} requires a transformation:\n${transformation.show()}")
    }
    // Retrieves the color in world space.
    abstract fun colorAt(worldPoint: Tuple): Color

    // Retrieves color for given shape at world space point.
    fun colorAtShape(shape: Shape, worldPoint: Tuple): Color {
        val localPoint = shape.worldToLocal(worldPoint)
        val patternPoint = transformation.inverse * localPoint
        return colorAt(patternPoint)
    }
}
