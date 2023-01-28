package math

// By Sebastian Raaphorst, 2023.

import shapes.Shape

internal sealed class OctTreeNode: TreeNode {
    companion object {
        // Make the bounding box into a cube.
        internal fun cubify(boundingBox: BoundingBox): BoundingBox {
            val minPoint = boundingBox.minPoint
            val maxPoint = boundingBox.maxPoint
            val minC = listOf(minPoint.x, minPoint.y, minPoint.z).min()
            val maxC = listOf(maxPoint.x, maxPoint.y, maxPoint.z).max()
            return BoundingBox(Tuple.point(minC, minC, minC), Tuple.point(maxC, maxC, maxC))
        }
    }
}

// In an OctBranch, we have:
// 1. The bounding box for the values contained in the subtree.
// 3. The eight trees rooted at this branch.
internal class OctTreeBranch(
    private val boundingBox: BoundingBox,
    private val children: List<OctTreeNode?>
): OctTreeNode() {
    override fun countNodes(): Long =
        1L + children.sumOf { it?.countNodes() ?: 0L }

    override val depth: Int by lazy {
        children.maxOf { it?.depth ?: -1 }
    }

    override fun localIntersect(rayLocal: Ray): List<Intersection> =
        if (boundingBox.intersects(rayLocal).isNotEmpty())
            children.flatMap { it?.localIntersect(rayLocal) ?: emptyList() }
        else
            emptyList()
}

// In an OctTreeLeaf, we have:
// 1. The bounding box for the values contained in this node.
// 2. The triangles represented in this node.
internal class OctTreeLeaf(
    override val depth: Int,
    private val boundingBox: BoundingBox,
    private val shapes: List<Shape>
): OctTreeNode() {
    override fun countNodes(): Long = 1L

    override fun localIntersect(rayLocal: Ray): List<Intersection> =
        if (boundingBox.intersects(rayLocal).isNotEmpty())
            shapes.flatMap { it.intersect(rayLocal) }
        else
            emptyList()
}

// Build an OctTreeNode based on the information provided. Parameters are:
// 1. Depth of the tree at this level.
// 2. BoundingBox for the volume of this tree.
// 3. Shapes represented in the subtree rooted at the returned node.
// 4. A stopping condition wherein a leaf will be returned.
// Note if child has no Shapes, null is returned.
internal fun buildOctTree(
    boundingBox: BoundingBox,
    shapes: List<Shape>,
    stoppingCondition: TreeStoppingCondition = TreeNode.stopAt(),
    depth: Int = 0,
    parentShapes: Int = Int.MAX_VALUE
): OctTreeNode? {
    // If there are no shapes, there is no point to continuing.
    if (shapes.isEmpty())
        return null

    // If we have not reduced the shapes or the stopping condition is reached, make a leaf.
    if (shapes.size == parentShapes || stoppingCondition(depth, shapes))
        return OctTreeLeaf(depth, boundingBox, shapes)

    // Split the bounding box into eight sub-boxes.
    // First find the middle point.
    val x = (boundingBox.minPoint.x + boundingBox.maxPoint.x) / 2
    val y = (boundingBox.minPoint.y + boundingBox.maxPoint.y) / 2
    val z = (boundingBox.minPoint.z + boundingBox.maxPoint.z) / 2

    val (minX, minY, minZ) = boundingBox.minPoint
    val (maxX, maxY, maxZ) = boundingBox.maxPoint

    // Create the eight sub-boxes.
    val subBoxes = listOf(
        BoundingBox(Tuple.point(minX, minY, minZ), Tuple.point(x, y, z)),
        BoundingBox(Tuple.point(minX, minY, z), Tuple.point(x, y, maxZ)),
        BoundingBox(Tuple.point(minX, y, minZ), Tuple.point(x, maxY, z)),
        BoundingBox(Tuple.point(minX, y, z), Tuple.point(x, maxY, maxZ)),
        BoundingBox(Tuple.point(x, minY, minZ), Tuple.point(maxX, y, z)),
        BoundingBox(Tuple.point(x, minY, z), Tuple.point(maxX, y, maxZ)),
        BoundingBox(Tuple.point(x, y, minZ), Tuple.point(maxX, maxY, z)),
        BoundingBox(Tuple.point(x, y, z), Tuple.point(maxX, maxY, maxZ))
    )

    // Determine which shapes intersect with which boxes.
    val shapeDivisions = subBoxes.associateWith { b ->
        shapes.filter { it.bounds.intersects(b) }
    }

    // Create the children of this node.
    val numShapes = shapes.size
    val children = shapeDivisions.entries
        .map { (b, s) -> buildOctTree(b, s, stoppingCondition, depth + 1, numShapes ) }
    return OctTreeBranch(boundingBox, children)
}
