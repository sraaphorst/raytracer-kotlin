package exec

import math.Tuple

// By Sebastian Raaphorst, 2022.

data class Projectile(val position: Tuple, val velocity: Tuple) {
    init {
        assert(position.isPoint())
        assert(velocity.isVector())
    }
}

data class Environment(val gravity: Tuple, val wind: Tuple) {
    init {
        assert(gravity.isVector())
        assert(wind.isVector())
    }
}

fun tick(environment: Environment, projectile: Projectile): Projectile {
    val position = projectile.position + projectile.velocity
    val velocity = projectile.velocity + environment.gravity + environment.wind
    return Projectile(position, velocity)
}

fun main() {
    val projectile = Projectile(Tuple.point(0, 1, 0), Tuple.vector(1, 1, 0).normalized)
    val environment = Environment(Tuple.vector(0, -0.1, 0), Tuple.vector(-0.01, 0, 0))
    val sequence = generateSequence(projectile) { tick(environment, it) }
        .map { it.position }
        .takeWhile { it.y >= 0 }
    sequence.forEach { println(it) }
    print("Number of ticks: ${sequence.count()}")
}
