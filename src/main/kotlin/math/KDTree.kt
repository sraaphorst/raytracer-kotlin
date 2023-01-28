package math

// By Sebastian Raaphorst, 2023.

import shapes.Triangle
import shapes.boundingBox
import kotlin.math.max

internal sealed class KDTreeNode: TreeNode {
    // k-d trees split on axes.
    enum class Axis: ((Triangle) -> Double) {
        X { override fun invoke(t: Triangle): Double = t.center.x },
        Y { override fun invoke(t: Triangle): Double = t.center.y },
        Z { override fun invoke(t: Triangle): Double = t.center.z }
    }
}

// In a KDTreeBranch, we have:
// 1. The bounding box for the values contained in the subtree rooted at this node.
// 2. The left tree rooted at this branch.
// 3. The right tree rooted at this branch.
internal class KDTreeBranch(
    private val boundingBox: BoundingBox,
    private val left: KDTreeNode,
    private val right: KDTreeNode,
): KDTreeNode() {
    override fun countNodes(): Long =
        1L + left.countNodes() + right.countNodes()

    override val depth: Int by lazy {
        max(left.depth, right.depth)
    }

    override fun localIntersect(rayLocal: Ray): List<Intersection> =
        // If we are in the bounding box, determine if we should check the left or right.
        if (boundingBox.intersects(rayLocal).isNotEmpty())
            // Median values could be on both sides for axis, so check both children.
            left.localIntersect(rayLocal) + right.localIntersect(rayLocal)
        else
            emptyList()
}

// In a KDLeaf, we have:
// 1. The bounding box for the values contained in this node.
// 2. The triangles represented in this node.
internal class KDTreeLeaf(
    override val depth: Int,
    private val boundingBox: BoundingBox,
    private val triangles: List<Triangle>
): KDTreeNode()  {
    override fun countNodes(): Long = 1L

    override fun localIntersect(rayLocal: Ray): List<Intersection> =
        if (boundingBox.intersects(rayLocal).isNotEmpty())
            triangles.flatMap { it.intersect(rayLocal) }
        else
            emptyList()
 }

// Build a KDTreeNode based on the information provided. Parameters are:
// 1. Depth of the tree at this level.
// 2. BoundingBox for the list of Triangles.
// 3. Triangles to be represented in the subtree rooted at the returned node.
// 4. A stopping condition wherein a leaf will be returned, e.g. # of triangles.
internal fun buildKDTree(
    boundingBox: BoundingBox,
    triangles: List<Triangle>,
    stoppingCondition: TreeStoppingCondition = TreeNode.stopAt(),
    depth: Int = 0
): KDTreeNode {
    if (stoppingCondition(depth, triangles))
        return KDTreeLeaf(depth, boundingBox, triangles)

    // Divide the triangles up according to axis dependent on depth.
    val axis = KDTreeNode.Axis.values()[depth % 3]
    val sortedTriangles = triangles.sortedBy { axis }

    // Get the median triangle.
    val medianIdx = triangles.size / 2

    // Divide into left and right lists.
    val leftTriangles = sortedTriangles.subList(0, medianIdx)
    val rightTriangles = sortedTriangles.subList(medianIdx, triangles.size)

    // Find the bounding boxes.
    val leftBoundingBox = leftTriangles.boundingBox()
    val rightBoundingBox = rightTriangles.boundingBox()

    val leftNode = buildKDTree(leftBoundingBox, leftTriangles, stoppingCondition, depth + 1)
    val rightNode = buildKDTree(rightBoundingBox, rightTriangles, stoppingCondition, depth + 1)

    return KDTreeBranch(boundingBox, leftNode, rightNode)
}
