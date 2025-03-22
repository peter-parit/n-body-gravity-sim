import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle
import scalafx.animation.AnimationTimer
import scalafx.collections.ObservableBuffer
import scala.collection.mutable.ListBuffer
import scalafx.scene.layout.Pane
import scalafx.scene.input.MouseEvent
import scalafx.Includes.*
import scalafx.stage.Screen
import scala.util.Random

object OptimizedSim extends JFXApp3 {

  // make a new body at (x, y) position
  def createCircle(x: Double, y: Double): Circle = {
    return Circle(x, y, 10, Color.White)
  }

  // main app window
  override def start(): Unit = {
    val screenBounds = Screen.primary.visualBounds
    val screenHeight = screenBounds.height
    val screenWidth = screenBounds.width
    stage = new JFXApp3.PrimaryStage {
      title = "N-Body Gravity Simulation"
      maximized = true
      resizable = true
      
      scene = new Scene {
        val main = new Pane {
          style = "-fx-background-color: Black;"
        }

        // create n bodies to the screen
        val NUM_BODIES = 10
        val BODY_MASS = 10e4
        val RADIUS = 5
        val random = new Random(123) // set seed for reproducibility (potentially when evaluating the run-time)

        val bodies: List[Body] = (0 until NUM_BODIES).map(_ => {
            new Body(random.nextDouble() * screenWidth, random.nextDouble() * screenHeight, BODY_MASS, RADIUS)
        }).toList
        val boundary = new Boundary(new Point(0.0, 0.0), new Point(screenWidth, screenHeight))
        val quadtree = new QuadTree(boundary)

        // inserting each body into the screen & quadtree
        bodies.foreach(body => {
            main.children.add(body.body)
            quadtree.insert(body)
        })

        println("Width: " + screenWidth)
        println("Height: "  + screenHeight)
        println((quadtree.topRightTree.topRightTree.topRightTree.body.get.x, quadtree.topRightTree.topRightTree.topRightTree.body.get.y))

        // start of simulation
        var lastTime = 0L
        // val timer = AnimationTimer { t =>
        //   if(lastTime > 0) {

        //     // update the positions of each body
        //     val elapsed = (t - lastTime) / 1e9

        //     bodies.foreach { node =>
        //       node.update(elapsed, bodies)
        //     }
        //   }
        //   lastTime = t
        // }
        // timer.start()

        root = main
      }
    }
  }
}