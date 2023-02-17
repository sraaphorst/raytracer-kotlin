package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.Color
import math.Matrix
import math.cartesianProduct
import kotlin.math.PI

// A function that creates a six sided die exactly in the bounding box (-1, -1, -1) to (1, 1, 1)
fun die6(bevelRadius: Double = 0.1,
         pipRadius: Double = 0.2,
         faceMaterial: List<Material> = List(6) { Material(Color.WHITE) },
         pipMaterial: List<Material> = List(6) { Material(Color.BLACK) },
         frameMaterial: Material = Material(Color.WHITE),
         pipOffset: Double = 0.55
): Group {
    // Make the solid parts of the die.
    val solid = run {
        // Make the frame of the die.
        val frame = run {
            val corner = Group(listOf(Sphere(Matrix.scale(bevelRadius, bevelRadius, bevelRadius))))
            val edge = Group(listOf(Cylinder(-2 + 2 * bevelRadius, 0, false,
                Matrix.scale(bevelRadius, 1, bevelRadius))))

            val offsets = listOf(1 - bevelRadius, -1 + bevelRadius)

            val corners = offsets.cartesianProduct(offsets).cartesianProduct(offsets).map { (vs, v3) ->
                val (v1, v2) = vs
                corner.withTransformation(Matrix.translate(v1, v2, v3))
            }

            val edge1 = edge.withTransformation(
                Matrix.translate(-1 + bevelRadius, 1 - bevelRadius, -1 + bevelRadius)
            )
            val edge2 = edge.withTransformation(
                Matrix.translate(1 - bevelRadius, 1 - bevelRadius, -1 + bevelRadius)
            )
            val edge3 = edge.withTransformation(
                Matrix.translate(-1 + bevelRadius, 1 - bevelRadius, 1 - bevelRadius)
            )
            val edge4 = edge.withTransformation(
                Matrix.translate(1 - bevelRadius, 1 - bevelRadius, 1 - bevelRadius)
            )
            val edge5 = edge.withTransformation(
                Matrix.translate(-1 + bevelRadius, 1 - bevelRadius, -1 + bevelRadius) *
                        Matrix.rotateZ(PI / 2)
            )
            val edge6 = edge.withTransformation(
                Matrix.translate(-1 + bevelRadius, 1 - bevelRadius, 1 - bevelRadius) *
                        Matrix.rotateZ(PI / 2)
            )
            val edge7 = edge.withTransformation(
                Matrix.translate(-1 + bevelRadius, -1 + bevelRadius, -1 + bevelRadius) *
                        Matrix.rotateZ(PI / 2)
            )
            val edge8 = edge.withTransformation(
                Matrix.translate(-1 + bevelRadius, -1 + bevelRadius, 1 - bevelRadius) *
                        Matrix.rotateZ(PI / 2)
            )
            val edge9 = edge.withTransformation(
                Matrix.translate(-1 + bevelRadius, -1 + bevelRadius, 1 - bevelRadius) *
                        Matrix.rotateX(PI / 2)
            )
            val edge10 = edge.withTransformation(
                Matrix.translate(1 - bevelRadius, -1 + bevelRadius, 1 - bevelRadius) *
                        Matrix.rotateX(PI / 2)
            )

            val edge11 = edge.withTransformation(
                Matrix.translate(-1 + bevelRadius, 1 - bevelRadius, 1 - bevelRadius) *
                        Matrix.rotateX(PI / 2)
            )
            val edge12 = edge.withTransformation(
                Matrix.translate(1 - bevelRadius, 1 - bevelRadius, 1 - bevelRadius) *
                        Matrix.rotateX(PI / 2)
            )

            val edges = listOf(
                edge1, edge2, edge3, edge4, edge5, edge6,
                edge7, edge8, edge9, edge10, edge11, edge12
            )

            Group(edges + corners).withMaterial(frameMaterial)
        }

        // Make the faces of the die. We do this with cubes as there is no way to use planes.
        val faces = run {
            val oneFace = Cube(Matrix.translate(0, 0, 0.5) *
                        Matrix.scale(1 - bevelRadius, 1 - bevelRadius, 0.5),
                faceMaterial[0]
            )

            val twoFace = Cube(Matrix.translate(0.5, 0, 0) *
                    Matrix.scale(0.5, 1 - bevelRadius, 1 - bevelRadius),
                faceMaterial[1]
            )

            val threeFace = Cube(Matrix.translate(0, 0.5, 0) *
                    Matrix.scale(1 - bevelRadius, 0.5, 1- bevelRadius),
                faceMaterial[2]
            )

            val fourFace = Cube(Matrix.translate(0, -0.5, 0) *
                    Matrix.scale(1 - bevelRadius, 0.5, 1 - bevelRadius),
                faceMaterial[3]
            )

            val fiveFace = Cube(Matrix.translate(-0.5, 0, 0) *
                Matrix.scale(0.5, 1 - bevelRadius, 1 - bevelRadius),
                faceMaterial[4]
            )

            val sixFace = Cube(Matrix.translate(0, 0, -0.5) *
                    Matrix.scale(1 - bevelRadius, 1 - bevelRadius, 0.5),
                faceMaterial[5]
            )

            Group(listOf(oneFace, sixFace, twoFace, fiveFace, threeFace, fourFace))
        }

        Group(listOf(frame, faces))
    }

    // The pips are then all assembled as a group, and we take the difference.
    val pips = run {
        // The pip is a tiny sphere
        val pip = Group(listOf(Sphere(Matrix.scale(pipRadius, pipRadius, pipRadius))))

        // The offset for pips from the edge of the die.
        val offset1 = -1 + pipOffset
        val offset2 = 1 - pipOffset

        val onePips = run {
            val center = pip.withTransformation(Matrix.translate(0, 0, 1))
            Group(listOf(center)).withMaterial(pipMaterial[0])
        }

        val twoPips = run {
            val corner1 = pip.withTransformation(Matrix.translate(1, offset1, offset2))
            val corner2 = pip.withTransformation(Matrix.translate(1, offset2, offset1))
            Group(listOf(corner1, corner2)).withMaterial(pipMaterial[1])
        }

        val threePips = run {
            val corner1 = pip.withTransformation(Matrix.translate(offset2, 1, offset1))
            val corner2 = pip.withTransformation(Matrix.translate(offset1, 1, offset2))
            val center   = pip.withTransformation(Matrix.translate(0, 1, 0))
            Group(listOf(corner1, corner2, center)).withMaterial(pipMaterial[2])
        }

        val fourPips = run {
            val corner1 = pip.withTransformation(Matrix.translate(offset1, -1, offset2))
            val corner2 = pip.withTransformation(Matrix.translate(offset1, -1, offset1))
            val corner3 = pip.withTransformation(Matrix.translate(offset2, -1, offset1))
            val corner4 = pip.withTransformation(Matrix.translate(offset2, -1, offset2))
            Group(listOf(corner1, corner2, corner3, corner4)).withMaterial(pipMaterial[3])
        }

        val fivePips = run {
            val corner1 = pip.withTransformation(Matrix.translate(-1, offset1, offset2))
            val corner2 = pip.withTransformation(Matrix.translate(-1, offset1, offset1))
            val corner3 = pip.withTransformation(Matrix.translate(-1, offset2, offset1))
            val corner4 = pip.withTransformation(Matrix.translate(-1, offset2, offset2))
            val center  = pip.withTransformation(Matrix.translate(-1, 0, 0))
            Group(listOf(corner1, corner2, corner3, corner4, center)).withMaterial(pipMaterial[4])
        }

        val sixPips = run {
            val corner1 = pip.withTransformation(Matrix.translate(offset1, offset1, -1))
            val corner2 = pip.withTransformation(Matrix.translate(offset1, offset2, -1))
            val corner3 = pip.withTransformation(Matrix.translate(offset2, offset1, -1))
            val corner4 = pip.withTransformation(Matrix.translate(offset2, offset2, -1))
            val edge1   = pip.withTransformation(Matrix.translate(offset1, 0, -1))
            val edge2   = pip.withTransformation(Matrix.translate(offset2, 0, -1))
            Group(listOf(corner1, corner2, corner3, corner4, edge1, edge2)).withMaterial(pipMaterial[5])
        }

        Group(listOf(onePips, twoPips, threePips, fourPips, fivePips, sixPips))
    }

    // The CSG is the difference between the entire solid die and the pips,
    // so that the pips will eat into the frame if necessary.
    return Group(listOf(CSGShape(Operation.Difference, solid, pips)))
}
