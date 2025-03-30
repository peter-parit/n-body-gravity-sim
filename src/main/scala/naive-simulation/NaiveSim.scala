package naivesimulation

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.paint.Color
import scalafx.animation.AnimationTimer
import scala.collection.mutable.ListBuffer
import scalafx.scene.layout.Pane
import scalafx.stage.Screen
import scala.util.Random
import scalafx.scene.text.Font

object NaiveSim extends JFXApp3 {

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
        val NUM_BODIES = 50000
        val BODY_MASS = 10e10
        val RADII = (.2,.4,.6).toList
        val EPSILON = 10
        val G = 6.67e-11
        val random = new Random(123) // set seed for reproducibility (potentially when evaluating the run-time)
        val circleRadius = screenHeight / 3
        val centersX = ((screenWidth / 2) - (screenWidth / 8), (screenWidth / 2) + (screenWidth / 8)).toList

        var bodies: ListBuffer[NaiveBody] = ListBuffer()
        for (_ <- 0.until(NUM_BODIES)) {
          val angle = random.nextDouble() * 2 * Math.PI
          val r = random.nextDouble() * circleRadius

          val newNaiveBody = new NaiveBody(
            centersX(random.nextInt(2)) + r * Math.cos(angle),
            (screenHeight / 2) + r * Math.sin(angle), 
            BODY_MASS,
            RADII(random.nextInt(3)),
            G
          )
          main.children.add(newNaiveBody.naiveBody)
          bodies.append(newNaiveBody)
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

        // starting of the animation on the screen
        val timer = AnimationTimer { t =>
          if(lastTime > 0) {

            // update fps
            if (t - lastFpsTime > 1e9) {
              fpsLabel.text = s"FPS: ${frameCount - 1} | Bodies: $NUM_BODIES"
              frameCount = 0
              lastFpsTime = t
            }
            frameCount += 1

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