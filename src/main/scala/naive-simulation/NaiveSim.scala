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
      title = "N-NaiveBody Gravity Simulation"
      maximized = true
      resizable = true
      
      scene = new Scene {
        val main = new Pane {
          style = "-fx-background-color: Black;"
        }


        // create n bodies to the screen
        val NUM_BODIES = 1000

        var bodies: ListBuffer[NaiveBody] = ListBuffer()
        for (_ <- 0 until NUM_BODIES) {
          val newNaiveBody = new NaiveBody(
            Math.random() * screenWidth,
            Math.random() * screenHeight,
            10e4,
            5
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
              node.update(elapsed, bodies)
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