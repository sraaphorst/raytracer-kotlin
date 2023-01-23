package math

// By Sebastian Raaphorst, 2023.

import shapes.Triangle
import shapes.boundingBox

internal sealed class KDNode {
    enum class Axis: ((Triangle) -> Double) {
        X { override fun invoke(t: Triangle): Double = t.center.x },
        Y { override fun invoke(t: Triangle): Double = t.center.y },
        Z { override fun invoke(t: Triangle): Double = t.center.z }
    }

    internal abstract fun localIntersect(rayLocal: Ray): List<Intersection>

    companion object {
        // Default value for the stopping point.
        private const val DefaultStoppingPoint = 10

        // Creates a stopping point function for division of the Group's objects into KDNodes.
        internal fun stopAt(num: Int = DefaultStoppingPoint): (List<Triangle>) -> Boolean =
            { it.size <= num }
    }
}


// In a KDBranch, we have:
// 1. The axis on which we split.
// 2. The bounding box for the values contained in the subtree rooted at this node.
// 3. The triangle represented by this node.
// 4. The left tree rooted at this branch.
// 5. The right tree rooted at this branch.
internal class KDBranch(
    private val boundingBox: BoundingBox,
    private val left: KDNode,
    private val right: KDNode,
): KDNode() {
    override fun localIntersect(rayLocal: Ray): List<Intersection> =
        // If we are in the bounding box, determine if we should check the left or right.
        if (boundingBox.intersects(rayLocal).isNotEmpty())
            // Not sure how to divide a ray, so check both left and right: one should be empty.
            left.localIntersect(rayLocal) + right.localIntersect(rayLocal)
        else
            emptyList()
}

// In a KDLeaf, we have:
// 1. The bounding box for the values contained in this node.
// 2. The triangles represented in this node.
internal class KDLeaf(
    private val boundingBox: BoundingBox,
    private val triangles: List<Triangle>
): KDNode()  {
    override fun localIntersect(rayLocal: Ray): List<Intersection> =
        if (boundingBox.intersects(rayLocal).isNotEmpty())
            triangles.flatMap { it.intersect(rayLocal) }
        else
            emptyList()
 }

// Build a KDNode based on the information provided. Parameters are:
// 1. Depth of the tree at this level.
// 1. BoundingBox for the list of Triangles.
// 2. Triangles to be represented in the subtree rooted at the returned KDNode.
// 3. A stopping condition wherein a KDLeaf will be returned, e.g. # of triangles.
internal fun buildKDTree(
    boundingBox: BoundingBox,
    triangles: List<Triangle>,
    stoppingCondition: (List<Triangle>) -> Boolean = KDNode.stopAt(),
    depth: Int = 0
): KDNode {
    if (stoppingCondition(triangles))
        return KDLeaf(boundingBox, triangles)

    // Divide the triangles up according to axis dependent on depth.
    val axis = KDNode.Axis.values()[depth % 3]
    val sortedTriangles = triangles.sortedBy { axis }

    // Get the median triangle.
    val medianIdx = triangles.size / 2

    // Divide into left and right lists.
    val leftTriangles = sortedTriangles.subList(0, medianIdx + 1)
    val rightTriangles = sortedTriangles.subList(medianIdx + 1, triangles.size)

    // Find the bounding boxes.
    val leftBoundingBox = leftTriangles.boundingBox()
    val rightBoundingBox = rightTriangles.boundingBox()

    val leftNode = buildKDTree(leftBoundingBox, leftTriangles, stoppingCondition, depth + 1)
    val rightNode = buildKDTree(rightBoundingBox, rightTriangles, stoppingCondition, depth + 1)

    return KDBranch(boundingBox, leftNode, rightNode)
}
