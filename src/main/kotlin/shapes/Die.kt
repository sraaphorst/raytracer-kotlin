package shapes

// By Sebastian Raaphorst, 2023.

import material.Material
import math.Color
import math.Matrix
import kotlin.math.PI

// A function that creates a six sided die exactly in the bounding box (-1, -1, -1) to (1, 1, 1)
fun die6(bevelRadius: Double = 0.1,
         pipRadius: Double = 0.2,
         faceMaterial: Material = Material(Color.WHITE),
         pipMaterial: Material = Material(Color.BLACK),
         pipOffset: Double = 0.55
): Group {
    // We want to reuse this cylinder multiple times, so we put it in a group.
    val edge = run {
        val edge = Cylinder(
            -2 + 2 * bevelRadius, 0, false,
            Matrix.scale(bevelRadius, 1, bevelRadius)
        )
        Group(listOf(edge))
    }
    // Branch occurs at (0, 0, 0) and travels down y.
    val branch = run {
        // We want the die to be 2x2x2 centered at the origin, so the branch (corner + edge) consists of:
        // 1. Sphere at (0.5 - bevelSize), scaled to bevelSize.
        // 2. Cylinder at (-1 + bevelSize) to (1 - bevelSize), scaled to bevelSize.
        // Note the branch is length 2 - bevelsize.
        val corner = Sphere(Matrix.scale(bevelRadius, bevelRadius, bevelRadius))
        Group(listOf(corner, edge))
    }

    // Make the frame of the die.
    val solid = run {
        val frame = run {
            val branch1 = branch.withTransformation(
                Matrix.translate(-1 + bevelRadius, 1 - bevelRadius, -1 + bevelRadius)
            )
            val branch2 = branch.withTransformation(
                Matrix.translate(1 - bevelRadius, 1 - bevelRadius, -1 + bevelRadius)
            )
            val branch3 = branch.withTransformation(
                Matrix.translate(-1 + bevelRadius, 1 - bevelRadius, 1 - bevelRadius)
            )
            val branch4 = branch.withTransformation(
                Matrix.translate(1 - bevelRadius, 1 - bevelRadius, 1 - bevelRadius)
            )
            val branch5 = branch.withTransformation(
                Matrix.translate(-1 + bevelRadius, 1 - bevelRadius, -1 + bevelRadius) *
                        Matrix.rotateZ(PI / 2)
            )
            val branch6 = branch.withTransformation(
                Matrix.translate(-1 + bevelRadius, 1 - bevelRadius, 1 - bevelRadius) *
                        Matrix.rotateZ(PI / 2)
            )
            val branch7 = branch.withTransformation(
                Matrix.translate(-1 + bevelRadius, -1 + bevelRadius, -1 + bevelRadius) *
                        Matrix.rotateZ(PI / 2)
            )
            val branch8 = branch.withTransformation(
                Matrix.translate(-1 + bevelRadius, -1 + bevelRadius, 1 - bevelRadius) *
                        Matrix.rotateZ(PI / 2)
            )
            val branch9 = branch.withTransformation(
                Matrix.translate(-1 + bevelRadius, -1 + bevelRadius, 1 - bevelRadius) *
                        Matrix.rotateX(PI / 2)
            )
            val branch10 = branch.withTransformation(
                Matrix.translate(1 - bevelRadius, -1 + bevelRadius, 1 - bevelRadius) *
                        Matrix.rotateX(PI / 2)
            )

            // Use cylinders only for the last two lines so that we don't repeat spheres.
            val edge11 = edge.withTransformation(
                Matrix.translate(-1 + bevelRadius, 1 - bevelRadius, 1 - bevelRadius) *
                        Matrix.rotateX(PI / 2)
            )
            val edge12 = edge.withTransformation(
                Matrix.translate(1 - bevelRadius, 1 - bevelRadius, 1 - bevelRadius) *
                        Matrix.rotateX(PI / 2)
            )

            Group(
                listOf(
                    branch1, branch2, branch3, branch4, branch5, branch6, branch7, branch8, branch9, branch10,
                    edge11, edge12
                )
            ).withMaterial(faceMaterial)
        }

        // Make the faces of the die. We do this with cubes as there is no way to use planes.
        val faces = run {
            val oneSixFace = Cube(
                Matrix.scale(1 - bevelRadius, 1 - bevelRadius, 1),
                Material(Color.RED)
            )

            val twoFiveFace = Cube(
                Matrix.scale(1, 1 - bevelRadius, 1 - bevelRadius),
                Material(Color.GREEN)
            )

            val threeFourFace = Cube(
                Matrix.scale(1 - bevelRadius, 1, 1 - bevelRadius),
                Material(Color.BLUE)
            )

            Group(listOf(oneSixFace, twoFiveFace, threeFourFace))
        }

        Group(listOf(frame, faces))
    }

    // The pips are then all assembled as a group and we take the difference.
    val pips = run {
        // The pip is a tiny sphere
        val pip = Group(listOf(Sphere(Matrix.scale(pipRadius, pipRadius, pipRadius), pipMaterial)))

        // A central pip.
        val centralPip = pip.withTransformation(Matrix.translate(0, 0, -1))

        // The offset for pips from the edge of the die.
        val offset1 = -1 + pipOffset
        val offset2 = 1 - pipOffset

        // An edge pip, which we need for six.
        val edgePip = pip.withTransformation(Matrix.translate(offset1, 0, -1))

        // A corner pip, which we can rotate around to get every other corner pip.
        val cornerPip = pip.withTransformation(Matrix.translate(offset1, offset1, -1))

        val testPips = Group(listOf(centralPip, edgePip, cornerPip))

        val sixPips = run {
            val lowerLeft = pip.withTransformation(Matrix.translate(offset1, offset1, -1))
            val left      = pip.withTransformation(Matrix.translate(offset1, 0, -1))
            val upperLeft = pip.withTransformation(Matrix.translate(offset1, offset2, -1))

            val lowerRight = pip.withTransformation(Matrix.translate(offset2, offset1, -1))
            val right      = pip.withTransformation(Matrix.translate(offset2, 0, -1))
            val upperRight = pip.withTransformation(Matrix.translate(offset2, offset2, -1))
            Group(listOf(lowerLeft, left, upperLeft, lowerRight, right, upperRight))
        }
//        Group(listOf(testPips))
        Group(listOf(sixPips))
    }

    // The CSG is the difference between the entire solid die and the pips,
    // so that the pips will eat into the frame if necessary.
    return Group(listOf(CSGShape(Operation.Difference, solid, pips)))
}
