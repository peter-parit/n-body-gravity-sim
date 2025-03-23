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

object NaiveSim extends JFXApp3 {

  // make a new NaiveBody at (x, y) position
  def createCircle(x: Double, y: Double): Circle = {
    return Circle(x, y, 10, Color.White)
  }

  // main app window
  override def start(): Unit = {
    val screenBounds = Screen.primary.visualBounds
    val screenHeight = screenBounds.height
    val screenWidth = screenBounds.width
    stage = new JFXApp3.PrimaryStage {
      title = "N-Body Naive Gravity Simulation"
      maximized = true
      resizable = true
      
      scene = new Scene {
        val main = new Pane {
          style = "-fx-background-color: Black;"
        }


        // create n bodies to the screen
        val NUM_BODIES = 2000
        val BODY_MASS = 10e11
        val RADIUS = 1
        val EPSILON = 10
        val G = 6.67e-11
        val random = new Random(123) // set seed for reproducibility (potentially when evaluating the run-time)
        val circleRadius = screenHeight / 2

        var bodies: ListBuffer[NaiveBody] = ListBuffer()
        for (_ <- 0 until NUM_BODIES) {
          val angle = random.nextDouble() * 2 * Math.PI
          val r = random.nextDouble() * circleRadius

          val newNaiveBody = new NaiveBody(
            (screenWidth / 2) + r * Math.cos(angle),
            (screenHeight / 2) + r * Math.sin(angle), 
            BODY_MASS,
            RADIUS,
            G
          )
          main.children.add(newNaiveBody.naiveBody)
          bodies.append(newNaiveBody)
        }
 
        // starting of the animation on the screen
        var lastTime = 0L
        val timer = AnimationTimer { t =>
          if(lastTime > 0) {

            // update the positions of each NaiveBody
            val elapsed = (t - lastTime) / 1e9

            bodies.foreach { node =>
              node.update(elapsed, bodies, EPSILON)
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