package exec

// By Sebastian Raaphorst, 2022.

import math.Tuple

fun main() {
    val projectile = Projectile(Tuple.point(0, 1, 0), Tuple.vector(1, 1, 0).normalized)
    val environment = Environment(Tuple.vector(0, -0.1, 0), Tuple.vector(-0.01, 0, 0))
    val sequence = generateSequence(projectile) { tick(environment, it) }
        .map { it.position }
        .takeWhile { it.y >= 0 }
    sequence.forEach { println(it) }
    print("Number of ticks: ${sequence.count()}")
}
