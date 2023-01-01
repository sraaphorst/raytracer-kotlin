package exec

// By Sebastian Raaphorst, 2022.

import math.Tuple

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