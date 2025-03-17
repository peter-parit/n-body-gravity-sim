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

object Main extends JFXApp3 {

  // make a new body at (x, y) position
  def createCircle(x: Double, y: Double): Circle = {
    return Circle(x, y, 10, Color.White)
  }

  // main app window
  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "N-Body Gravity Simulation"
      width = 880
      height = 660
      maximized = false
      resizable = false
      
      scene = new Scene {
        val main = new Pane {
          style = "-fx-background-color: Black;"
          prefWidth = 880
          prefHeight = 660
        }
        var bodies = ListBuffer(new Body(200, 200, 10e4, 10), new Body(440, 330, 10e5, 20))
        bodies.foreach(node => main.children.add(node.body))

        var lastTime = 0L
        val timer = AnimationTimer { t =>
          if(lastTime > 0) {

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