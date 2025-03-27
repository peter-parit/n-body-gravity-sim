package optimizedsimulation

class Point(var x: Double, var y: Double) {
    
    // default constructor
    def this() = {
        this(0.0, 0.0)
    }
}

class Boundary(var topLeft: Point, var bottomRight: Point) {

    // check if point is in boundary
    def isConfined(p: Point): Boolean = {
        return p.x >= topLeft.x 
            && p.x <= bottomRight.x 
            && p.y >= topLeft.y 
            && p.y <= bottomRight.y
    }
}

class QuadTree(var boundary: Boundary, var body: Option[ParBody] = None, 
                   var topLeftTree: QuadTree = null, var topRightTree: QuadTree = null, 
                   var bottomLeftTree: QuadTree = null, var bottomRightTree: QuadTree = null, val depth: Int = 0) {

    var comX = 0.0
    var comY = 0.0
    var totalMass = 0.0

    // subdivide into 4 quadrants
    def subdivide(): Unit = {
        val topLeft = boundary.topLeft
        val bottomRight = boundary.bottomRight

        val width = (bottomRight.x - topLeft.x) / 2
        val length = (bottomRight.y - topLeft.y) / 2

        val midX = topLeft.x + width
        val midY = topLeft.y + length

        // create quadrants with new boundaries
        topLeftTree = new QuadTree(new Boundary(new Point(topLeft.x, topLeft.y), new Point(midX, midY)),
        None, null, null, null, null, depth + 1)
        topRightTree = new QuadTree(new Boundary(new Point(midX, topLeft.y), new Point(bottomRight.x, midY)),
        None, null, null, null, null, depth + 1)
        bottomLeftTree = new QuadTree(new Boundary(new Point(topLeft.x, midY), new Point(midX, bottomRight.y)),
        None, null, null, null, null, depth + 1)
        bottomRightTree = new QuadTree(new Boundary(new Point(midX, midY), new Point(bottomRight.x, bottomRight.y)),
        None, null, null, null, null, depth + 1)
    }

    // update the center of mass 
    def updateCOM(body: ParBody): Unit = {

        comX = (totalMass * comX + body.mass * body.x) / (totalMass + body.mass)
        comY = (totalMass * comY + body.mass * body.y) / (totalMass + body.mass)

        totalMass += body.mass
    }

    // insert a new node to the tree
    def insert(body: ParBody): Boolean = {

        // check if out of bounds
        if (!boundary.isConfined(new Point(body.x, body.y))) then return false

        // check if no point in quadrant and no children
        if (this.body == None && topLeftTree == null) {
            this.body = Some(body)
            updateCOM(body)
            return true
        }

        // check if no children but point exists, subdivide
        if (topLeftTree == null) {
            subdivide()
            
            val existingPoint = this.body.get
            this.body = None

            // re-insert the node from its parent to inside the quadrants
            insert(existingPoint)
        }

        // insert body to correct quadrant and return true if successful
        val inserted = topLeftTree.insert(body) || topRightTree.insert(body)
                    || bottomLeftTree.insert(body) || bottomRightTree.insert(body)

        // udpate center of mass if body is inserted
        if (inserted) { updateCOM(body) }
        
        return inserted
    }
}