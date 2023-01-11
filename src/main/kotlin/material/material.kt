package material

// By Sebastian Raaphorst, 2023.

import light.Light
import math.Color
import math.Tuple
import math.almostEquals
import pattern.Pattern
import pattern.SolidPattern
import shapes.Shape
import kotlin.math.pow

data class Material(val pattern: Pattern = SolidPattern(Color.WHITE),
                    val ambient: Double = DEFAULT_AMBIENT,
                    val diffuse: Double = DEFAULT_DIFFUSE,
                    val specular: Double = DEFAULT_SPECULAR,
                    val shininess: Double = DEFAULT_SHININESS,
                    val reflectivity: Double = DEFAULT_REFLECTIVITY) {

    // Convenience constructor to create a material with a solid pattern.
    constructor(color: Color,
                ambient: Double = DEFAULT_AMBIENT,
                diffuse: Double = DEFAULT_DIFFUSE,
                specular: Double = DEFAULT_SPECULAR,
                shininess: Double = DEFAULT_SHININESS):
            this(SolidPattern(color), ambient, diffuse, specular, shininess)

    fun lighting(shape: Shape,
                 light: Light,
                 point: Tuple,
                 eyeV: Tuple,
                 normalV: Tuple,
                 inShadow: Boolean): Color {
        // Combine surface color with light's color / intensity.
        val effectiveColor = pattern.colorAtShape(shape, point) * light.intensity

        // Compute ambient contribution.
        // If the point is in shadow, we only want the ambient component.
        val ambient = effectiveColor * ambient
        if (inShadow)
            return ambient

        // Find direction to the light source.
        val lightV = (light.position - point).normalized

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

        if (pattern != other.pattern) return false
        if (!almostEquals(ambient, other.ambient)) return false
        if (!almostEquals(diffuse, other.diffuse)) return false
        if (!almostEquals(specular, other.specular)) return false
        if (!almostEquals(shininess, other.shininess)) return false

        return true
    }

    override fun hashCode(): Int =
        31 * (31 * (31 * (31 * pattern.hashCode() + ambient.hashCode()) +
                diffuse.hashCode()) + specular.hashCode()) + shininess.hashCode()

    companion object {
        const val DEFAULT_AMBIENT = 0.1
        const val DEFAULT_DIFFUSE = 0.9
        const val DEFAULT_SPECULAR = 0.9
        const val DEFAULT_SHININESS = 200.0
        const val DEFAULT_REFLECTIVITY = 0.0
    }
}
