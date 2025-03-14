object QuadTreeTest {
    def main(args: Array[String]): Unit = {
        val b: Boundary = new Boundary(new Point(0.0, 0.0), new Point(2.0, 2.0))
        val tree: QuadTree = new QuadTree(b)
        if(tree.insert(new Point(0.5, 0.5))) then println("Added Point(0.5, 0.5)")
        if(tree.insert(new Point(0.5, 1.5))) then println("Added Point(0.5, 1.5)")
        if(tree.insert(new Point(0.4, 0.2))) then println("Added Point(0.4, 0.2)")
        println((tree.topLeftTree.point.get.x, tree.topLeftTree.point.get.y))
        println((tree.bottomLeftTree.bottomLeftTree.point.get.x, tree.bottomLeftTree.bottomLeftTree.point.get.y))
    }
}
