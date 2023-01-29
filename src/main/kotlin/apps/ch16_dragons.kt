package apps

// By Sebastian Raaphorst, 2023.

import math.Tuple
import scene.Camera

fun main() {
    val world = run {

    }

    val camera = run {
        val from = Tuple.point(0, 2.5, -10)
        val to = Tuple.PY
        val up = Tuple.VY
        val t = from.viewTransformationFrom(to, up)
        Camera(1200, 480, 1.2, t)
    }
}