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

object Main extends JFXApp3 {

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
        // var bodies = ListBuffer(new Body(200, 200, 10e5, 10), new Body(440, 330, 10e5, 10), new Body(600, 700, 10e4, 10))
        var bodies: ListBuffer[Body] = ListBuffer()

        for (_ <- 0 until 1000) {
          val newBody = new Body(
            Math.random() * screenWidth,
            Math.random() * screenHeight,
            10e4,
            5
          )
          main.children.add(newBody.body)
          bodies.append(newBody)
        }
 
        var lastTime = 0L
        val timer = AnimationTimer { t =>
          if(lastTime > 0) {

            // mouseEvent to add new bodies
            // main.onMouseClicked = (me: MouseEvent) => {
            //   for (i <- 0 until 1000) {
            //     val newBody = new Body(me.x, me.y, 10e4, 10)
            //     main.children.add(newBody.body)
            //     bodies.append(newBody)
            //   }
            // }

            // update the positions of each body
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