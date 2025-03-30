import optimizedsimulation._
import naivesimulation._
import scala.util.Random
import scala.collection.mutable.ListBuffer

object NaiveSimTest {

    def run(n: Int): Double = {
        // constants
        val random = new Random(123)
        val G = 6.67e-11
        val EPSILON = 10 
        val screenHeight = 1032.0
        val screenWidth = 1920.0
        val circleRadius = screenHeight / 3
        val centersX = ((screenWidth / 2) - (screenWidth / 8), (screenWidth / 2) + (screenWidth / 8)).toList

        // testing variables
        val NUM_BODIES = n
        val BODY_MASS = 10e15
        val RADII = (5, 10, 15).toList

        // create n bodies with different positions
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

          bodies += newNaiveBody
        }

        // start of simulation
        var lastTime = System.nanoTime()
        var counter = 0
        val startTime = System.nanoTime()

        while(counter < 10) {
            // update the positions of each body
            val elapsed = (System.nanoTime() - lastTime) / 1e9.toFloat

            bodies.foreach { node =>
              node.update(elapsed, bodies, EPSILON)
            }

            counter += 1
            println(counter)
            lastTime = System.nanoTime()
        }

        val totalTime = (System.nanoTime() - startTime) / 1e9
        totalTime
    }
}
