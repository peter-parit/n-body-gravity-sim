object QuadTreeTest {
    def main(args: Array[String]): Unit = {
        val b: Boundary = new Boundary(new Point(0.0, 0.0), new Point(2.0, 2.0))
        val tree: QuadTree = new QuadTree(b)
        if(tree.insert(new ParBody(0.5, 0.5, 10, 10))) then println("Added Point(0.5, 0.5)") // .
        if(tree.insert(new ParBody(0.5, 1.5, 10, 10))) then println("Added Point(0.5, 1.5)") // #
        if(tree.insert(new ParBody(0.4, 1.8, 10, 10))) then println("Added Point(0.4, 0.2)") // *
        println((tree.topLeftTree.body.get.x, tree.topLeftTree.body.get.y))
        println((tree.bottomLeftTree.bottomLeftTree.body.get.x,
                 tree.bottomLeftTree.bottomLeftTree.body.get.y))

        /* Rough Sketch of the tree above: (scalafx's positioning has (0,0) at top left.)
        ________________
        |       |       |
        |   .   |       |
        |       |       |
        |_______|_______|
        |   |   |       |
        |___#___|       |
        |   |   |       |
        |__*|___|_______|
       
        */
    }
}
