package org.vorpal.raytracer.input

import java.net.URL

object ResourceManager {
    fun getResource(name: String): URL =
        requireNotNull(OBJParser::class.java.getResource(name)
        ) { """Resource "$name" not found on classpath""" }
}