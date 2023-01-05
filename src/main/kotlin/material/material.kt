package material

// By Sebastian Raaphorst, 2023.

import light.Light
import math.Color
import math.Tuple
import math.almostEquals
import kotlin.math.pow

data class Material(val color: Color = Color.WHITE,
                    val ambient: Double = 0.1,
                    val diffuse: Double = 0.9,
                    val specular: Double = 0.9,
                    val shininess: Double = 200.0) {

    fun lighting(light: Light, point: Tuple, eyeV: Tuple, normalV: Tuple): Color {
        // Combine surface color with light's color / intensity.
        val effectiveColor = color * light.intensity

        // Find direction to the light source.
        val lightV = (light.position - point).normalized

        // Compute ambient contribution.
        val ambient = effectiveColor * ambient

        // lightDotNormal represents cosine of the angle between lightV and normalV.
        // A negative number means the light is on the other side of the surface.
        val lightDotNormal = lightV.dot(normalV)
        val diffuse = if (lightDotNormal < 0) Color.BLACK else effectiveColor * diffuse * lightDotNormal
        val specular = if (lightDotNormal < 0) Color.BLACK else {
            val reflectV = -lightV.reflect(normalV)
            val reflectDotEye = reflectV.dot(eyeV)
            if (reflectDotEye < 0) Color.BLACK else light.intensity * specular * reflectDotEye.pow(shininess)
        }

        return ambient + diffuse + specular
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Material) return false

        if (!almostEquals(color, other.color)) return false
        if (!almostEquals(ambient, other.ambient)) return false
        if (!almostEquals(diffuse, other.diffuse)) return false
        if (!almostEquals(specular, other.specular)) return false
        if (!almostEquals(shininess, other.shininess)) return false

        return true
    }

    override fun hashCode(): Int =
        31 * (31 * (31 * (31 * color.hashCode() + ambient.hashCode()) +
                diffuse.hashCode()) + specular.hashCode()) + shininess.hashCode()
}
