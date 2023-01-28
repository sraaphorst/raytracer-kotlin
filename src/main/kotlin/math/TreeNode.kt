package math

import shapes.Shape

// By Sebastian Raaphorst, 2023.
// The interface for KDTrees and OctTrees.

// Stopping condition for recursion:
// 1. Recursive depth.
// 2. List of triangles in this node.
internal typealias TreeStoppingCondition = (Int, List<Shape>) -> Boolean

internal interface TreeNode {
    fun localIntersect(rayLocal: Ray): List<Intersection>
    fun countNodes(): Long
    val depth: Int

    companion object {
        // Max depth of the tree.
        internal const val MaxDepth = 1000

        // If there are these many children in a node, it is a leaf.
        internal const val ChildThreshold = 1

        // Create a TreeStoppingCondition.
        internal fun stopAt(depth: Int = MaxDepth,
                            num: Int = ChildThreshold): TreeStoppingCondition =
            { nodeDepth, nodeTriangles -> nodeDepth > depth || nodeTriangles.size <= num }
    }
}
