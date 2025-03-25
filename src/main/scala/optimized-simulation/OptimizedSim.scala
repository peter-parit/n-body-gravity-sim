package optimizedsimulation

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
import scala.collection.parallel.CollectionConverters.*

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
      title = "N-Body Parallel Gravity Simulation"
      maximized = true
      resizable = true
      
      scene = new Scene {
        val main = new Pane {
          style = "-fx-background-color: Black;"
        }

        // initializing variables
        val NUM_BODIES = 10000
        val BODY_MASS = 10e11
        val G = 6.67e-11
        val RADII = (0.3, 0.5, 0.7).toList
        val THETA = 1.0 // threshold for barnes hut algorithm
        val EPSILON = 10 // softening length (prevents division by 0)
        val random = new Random(123) // set seed for reproducibility (potentially when evaluating the run-time)
        val circleRadius = screenHeight / 3
        val centersX = ((screenWidth / 2) - (screenWidth / 12), (screenWidth / 2) + (screenWidth / 12)).toList
        // val centersY = (screenHeight / 2, (screenHeight / 2) + 200).toList

        // create n bodies to the screen
        val bodies: List[ParBody] = (0 until NUM_BODIES).map(_ => {
          val angle = random.nextDouble() * 2 * Math.PI
          val r = random.nextDouble() * circleRadius
          new ParBody(
            centersX(random.nextInt(2)) + r * Math.cos(angle),
            (screenHeight / 2) + r * Math.sin(angle), 
            BODY_MASS, 
            RADII(random.nextInt(3)), 
            G
            )}).toList
        val boundary = new Boundary(new Point(0.0, 0.0), new Point(screenWidth, screenHeight))
        val quadtree = new QuadTree(boundary)

        // inserting each body into the screen & quadtree
        bodies.foreach(body => {
            main.children.add(body.parBody)
            quadtree.insert(body)
        })

        /*
        checking if the quadtree is being inserted correctly
        println("Width: " + screenWidth)
        println("Height: " + screenHeight)
        println((quadtree.topRightTree.topRightTree.topRightTree.body.get.x, quadtree.topRightTree.topRightTree.topRightTree.body.get.y))
        println("Center of mass.x: " + quadtree.topLeftTree.comX)
        */

        // start of simulation  
        var lastTime = 0L
        val timer = AnimationTimer { t =>
          if(lastTime > 0) {

            // update the positions of each body
            val elapsed = (t - lastTime) / 1e9
            bodies.foreach(body => {
              body.update(elapsed, quadtree, THETA, EPSILON)
            })
          }
          lastTime = t
        }
        timer.start()

        root = main
      }
    }
  }
}