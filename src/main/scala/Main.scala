import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color

object Main extends JFXApp3 {
  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "N-Body Gravity Simulation"

      // set screen properties
      width = 1200 
      height = 900
      maximized = false
      resizable = false
      
      scene = new Scene {
        root = new StackPane {
          // set container's background to black
          style = "-fx-background-color: Black;"
          children = new Label("Testing ScalaFX")
        }
      }
    }
  }
}