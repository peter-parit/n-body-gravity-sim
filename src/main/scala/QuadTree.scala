private class Point(var x: Float, var y: Float) {
    
    // default constructor
    def this() = {
        this(0.0, 0.0)
    }
}

private class Boundary(var bottomLeft: Point, var topRight: Point) {

    // check if point is in boundary
    def isConfined(p: Point): Boolean = {
        return p.x >= bottomLeft.x 
            && p.x <= topRight.x 
            && p.y >= bottomLeft.y 
            && p.y <= topRight.y
    }
}

class QuadTree(var boundary: Boundary, var point: Option[Point] = None, 
                   var topLeftTree: QuadTree = null, var topRightTree: QuadTree = null, 
                   var bottomLeftTree: QuadTree = null, var bottomRightTree: QuadTree = null) {

    // subdivide into 4 quadrants
    def subdivide(): Unit = {
        val bottomLeft = boundary.bottomLeft
        val topRight = boundary.topRight

        val width = (topRight.x - bottomLeft.x) / 2
        val length = (topRight.y - bottomLeft.y) / 2

        val midX = bottomLeft.x + width
        val midY = bottomLeft.y + length

        // create quadrants with new boundaries
        topLeftTree = new QuadTree(new Boundary(new Point(bottomLeft.x, midY), new Point(midX, topRight.y)))
        topRightTree = new QuadTree(new Boundary(new Point(midX, midY), new Point(topRight.x, topRight.y)))
        bottomLeftTree = new QuadTree(new Boundary(new Point(bottomLeft.x, bottomLeft.y), new Point(midX, midY)))
        bottomRightTree = new QuadTree(new Boundary(new Point(midX, bottomLeft.y), new Point(topRight.y, midY)))
    }

    // insert a new node to the tree
    def insert(node: Point): Boolean = {

        // check if out of bounds
        if(!boundary.isConfined(node)) then return false

        // check if no point in quadrant and no children
        if(point == None && topLeftTree == null) {
            point = Some(node)
            return true
        }

        // check if no children but point exists, subdivide
        if(topLeftTree == null) {
            subdivide()

            val existingPoint = point.get
            point = None

            // re-insert the node from its parent to inside the quadrants
            insert(existingPoint)
        }

        // insert at the correct quadrant through recursion
        if(topLeftTree.insert(node)) then return true
        if(topRightTree.insert(node)) then return true
        if(bottomLeftTree.insert(node)) then return true
        if(bottomRightTree.insert(node)) then return true
        else return false
    }
}