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

class Material(val pattern: Pattern = SolidPattern(Color.WHITE),
               ambient: Number = DEFAULT_AMBIENT,
               diffuse: Number = DEFAULT_DIFFUSE,
               specular: Number = DEFAULT_SPECULAR,
               shininess: Number = DEFAULT_SHININESS,
               reflectivity: Number = DEFAULT_REFLECTIVITY,
               transparency: Number = DEFAULT_TRANSPARENCY,
               refractiveIndex: Number = DEFAULT_REFRACTIVE_INDEX) {

    val ambient = ambient.toDouble()
    val diffuse = diffuse.toDouble()
    val specular = specular.toDouble()
    val shininess = shininess.toDouble()
    val reflectivity = reflectivity.toDouble()
    val transparency = transparency.toDouble()
    val refractiveIndex = refractiveIndex.toDouble()

    // Convenience constructor to create a material with a solid pattern.
    constructor(color: Color,
                ambient: Number = DEFAULT_AMBIENT,
                diffuse: Number = DEFAULT_DIFFUSE,
                specular: Number = DEFAULT_SPECULAR,
                shininess: Number = DEFAULT_SHININESS,
                reflectivity: Number = DEFAULT_REFLECTIVITY,
                transparency: Number = DEFAULT_TRANSPARENCY,
                refractiveIndex: Number = DEFAULT_REFRACTIVE_INDEX):
            this(SolidPattern(color),
                ambient, diffuse, specular, shininess, reflectivity, transparency, refractiveIndex)

    internal fun lighting(shape: Shape,
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

    companion object {
        const val DEFAULT_AMBIENT = 0.1
        const val DEFAULT_DIFFUSE = 0.9
        const val DEFAULT_SPECULAR = 0.9
        const val DEFAULT_SHININESS = 200.0
        const val DEFAULT_REFLECTIVITY = 0.0
        const val DEFAULT_TRANSPARENCY = 0.0
        const val DEFAULT_REFRACTIVE_INDEX = 1.0
    }
}
