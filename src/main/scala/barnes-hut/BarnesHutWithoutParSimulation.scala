package barneshut

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.paint.Color
import scalafx.animation.AnimationTimer
import scalafx.scene.layout.Pane
import scalafx.stage.Screen.primary
import scala.util.Random
import scalafx.application.Platform
import scalafx.scene.text.Font
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object BarnesHutWithoutParSimulation extends JFXApp3 {

  // main app window
  override def start(): Unit = {
    val screenBounds = primary.visualBounds
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
        val NUM_BODIES = 100000
        val BODY_MASS = 10e9
        val G = 6.67e-11
        val RADII = (.2,.4,.6).toList
        val THETA = 1.0 // threshold for barnes hut algorithm
        val EPSILON = 10 // softening length (prevents division by 0)
        val random = new Random(123) // set seed for reproducibility (potentially when evaluating the run-time)
        val circleRadius = screenHeight / 3
        val centersX = ((screenWidth / 2) - (screenWidth / 8), (screenWidth / 2) + (screenWidth / 8)).toList

        // create n bodies to the screen
        val bodies = (0.until(NUM_BODIES)).map(_ => {
          val angle = random.nextDouble() * 2 * Math.PI
          val r = random.nextDouble() * circleRadius
          new ParBody(
            centersX(random.nextInt(2)) + r * Math.cos(angle),
            (screenHeight / 2) + r * Math.sin(angle), 
            BODY_MASS, 
            RADII(random.nextInt(3)), 
            G
            )}).toVector

        // inserting each body into the screen & quadtree
        Platform.runLater {
          bodies.foreach(body => { main.children.add(body.parBody) })
        }

        // fps counter
        val fpsLabel = new Label("FPS: 0")
        fpsLabel.font = Font.font(24)
        fpsLabel.setTextFill(Color.White)
        main.children.add(fpsLabel)

        // init variables
        var lastTime = 0L
        var frameCount = 0
        var lastFpsTime = 0L

        // start of simulation
        val timer = AnimationTimer { t =>
          if(lastTime > 0) {

            // update fps
            if (t - lastFpsTime > 1e9) {
              fpsLabel.text = s"FPS: ${frameCount - 1} | Bodies: $NUM_BODIES"
              frameCount = 0
              lastFpsTime = t
            }
            frameCount += 1

            // make quadtree for each body so no values are shared
            val boundary = new Boundary(new Point(0.0, 0.0), new Point(screenWidth, screenHeight))
            val tree = new QuadTree(boundary)
            bodies.foreach(tree.insert)

            // update the positions of each body
            val elapsed = (t - lastTime) / 1e9
            
            bodies.map { body =>
              val (fx, fy) = body.calculateForce(tree, THETA, EPSILON)
              body.updatePhysics(elapsed, fx, fy)
              body.updatePosition()
            }
          }
          lastTime = t
        }
        timer.start()

        root = main
      }
    }
  }
}