package input

// By Sebastian Raaphorst, 2023.

import math.Tuple
import shapes.Group
import shapes.Triangle
import java.io.*
import java.net.URI
import java.net.URL

class OBJParser(reader: Reader) {
    data class TextureCoordinate(val u: Double, val v: Double)
    data class Face(val pointIndices: List<Int>, val textureCoordinateIndices: List<Int>, val normalIndices: List<Int>)

    // This contains all the necessary parsed data.
    val groups: Map<String, Group>

    // This contains the data that could not be parsed.
    val failedLines: List<String>

    internal val points: Map<Int, Tuple>
    private val textureCoordinates: List<TextureCoordinate>
    private val normals: List<Tuple>
    private val faces: List<Face>

    init {
        val points = mutableListOf<Tuple>()
        val textureCoordinates = mutableListOf<TextureCoordinate>()
        val normals = mutableListOf<Tuple>()

        // Name of the group to the indices to a list of indices of faces.
        // By default, supply an empty mutable set.
        val namedGroupIndices = mutableMapOf<String, MutableList<Int>>()

        val faces = mutableListOf<Face>()
        val failedLines = mutableListOf<String>()

        // We need to keep track of the current group.
        var currentGroup = DefaultGroup

        reader.forEachLine { line ->
            val elements = line.split(" ")
            when (elements[0]) {
                "v" -> {
                    if (elements.size != 4)
                        throw IllegalArgumentException("Illegal vertex string: $line.")
                    val (p1, p2, p3) = try {
                        elements.drop(1).map(String::toDouble)
                    } catch (_: NumberFormatException) {
                        throw IllegalArgumentException("Illegal coordinate in vertex string: $line.")
                    }
                    points.add(Tuple.point(p1, p2, p3))
                }

                "vt" -> {
                    if (elements.size != 3)
                        throw IllegalArgumentException("Illegal texture coordinate string: $line.")
                    val (tc1, tc2) = try {
                        Pair(elements[1].toDouble(), elements[2].toDouble())
                    } catch (_: NumberFormatException) {
                        throw IllegalArgumentException("Illegal coordinate in texture coordinate string: $line.")
                    }
                    textureCoordinates.add(TextureCoordinate(tc1, tc2))
                }

                "vn" -> {
                    if (elements.size != 4)
                        throw IllegalArgumentException("Illegal normal string: $line.")
                    val (n1, n2, n3) = try {
                        elements.drop(1).map(String::toDouble)
                    } catch (_: NumberFormatException) {
                        throw IllegalArgumentException("Illegal coordinate in normal string: $line.")
                    }
                    normals.add(Tuple.vector(n1, n2, n3))
                }

                "g" -> {
                    if (elements.size > 2)
                        throw IllegalArgumentException("Too many elements to group name: $line.")

                    // If we just have g, this is the default group.
                    // Otherwise, assign the new group name.
                    currentGroup = elements.getOrElse(1) { DefaultGroup }
                }

                "f" -> {
                    val pointIndices = mutableListOf<Int>()
                    val textureCoordinateIndices = mutableListOf<Int>()
                    val normalIndices = mutableListOf<Int>()

                    val remaining = elements.drop(1)
                    if (elements.size < 3)
                        throw IllegalArgumentException(
                            "Face ${toFace(elements)} does not have enough vertices.")

                    remaining.forEach {
                        val pointData = it.split("/")

                        val pointIdx = try {
                            pointData[0].toInt()
                        } catch (_: NumberFormatException) {
                            throw IllegalArgumentException(
                                "Face ${toFace(remaining)} contains illegal vertex ${pointData[0]}."
                            )
                        }

                        if (pointIdx > points.size)
                            throw IllegalArgumentException(
                                "Undefined vertex index $pointIdx in face ${toFace(remaining)}.")
                        pointIndices.add(pointIdx)

                        if (pointData.size > 1 && pointData[1].isNotEmpty()) {
                            val tci = try {
                                pointData[1].toInt()
                            } catch (_: NumberFormatException) {
                                throw IllegalArgumentException(
                                    "Illegal texture coordinate ${pointData[1]} in face ${toFace(remaining)}"
                                )
                            }
                            textureCoordinateIndices.add(tci)
                        }

                        if (pointData.size > 2 && pointData[2].isNotEmpty()) {
                            val ni = try {
                                pointData[2].toInt()
                            } catch (_: NumberFormatException) {
                                throw IllegalArgumentException(
                                    "Illegal normal index ${pointData[2]} in face ${toFace(remaining)}."
                                )
                            }
                            normalIndices.add(ni)
                        }
                    }

                    // Create the new face and add it to the active group.
                    faces.add(Face(pointIndices.toList(), textureCoordinateIndices.toList(), normalIndices.toList()))

                    // Add the face to the current group. Note that withDefault doesn't work here.
                    namedGroupIndices.putIfAbsent(currentGroup, mutableListOf())
                    namedGroupIndices.getValue(currentGroup).add(faces.size - 1)
                }

                "#" -> { /* Comment. Do Nothing. */ }

                else -> failedLines.add(line)
            }
        }

        // Advance the points by 1 since we begin at 1.
        this.points = points.withIndex().associate { (idx, v) -> ((idx + 1) to v) }
        this.textureCoordinates = textureCoordinates.toList()
        this.normals = normals.toList()
        this.faces = faces.toList()
        this.failedLines = failedLines.toList()

        // Create the groups.
        this.groups = namedGroupIndices.entries.associate { (name, faceIndices) ->
            (name to Group(faceIndices.flatMap { makeFace(faces[it]) }))
        }
    }

    private fun makeFace(face: Face): List<Triangle> {
        // Retrieve the points themselves.
        val points = face.pointIndices.map { points.getValue(it) }

        // Create the fan pattern of (v1, v2, v3), (v1, v3, v4), etc.
        val p1 = points.first()
        val remainingPoints = points.drop(1)
        return remainingPoints.zipWithNext().map { (p2, p3) -> Triangle(p1, p2, p3) }
    }

    companion object {
        const val DefaultGroup = "default"

        private fun toFace(lst: List<String>): String =
            "(${lst.joinToString(",")})"

        fun fromInputStream(stream: InputStream) =
            OBJParser(BufferedReader(InputStreamReader(stream)))

        fun fromFilePath(filePath: String) =
            fromInputStream(FileInputStream(File(filePath)))

        fun fromText(text: String) =
            OBJParser(BufferedReader(StringReader(text)))

        fun fromURL(url: URL) =
            fromInputStream(url.openStream())

        fun fromURI(uri: URI) =
            fromURL(uri.toURL())
    }
}
