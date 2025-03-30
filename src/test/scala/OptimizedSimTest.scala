import optimizedsimulation._
import naivesimulation._
import scala.util.Random
import scala.collection.parallel.CollectionConverters._
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

object OptimizedSimTest {

    def run(n: Int): Double = {
        // constants
        val random = new Random(123)
        val G = 6.67e-11
        val THETA = 1.0 
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

        // start of simulation
        var lastTime = System.nanoTime()
        var counter = 0
        val startTime = System.nanoTime()

        while(counter <= 10) {
            val elapsed = (System.nanoTime() - lastTime) / 1e9.toFloat

            // Create quadtree
            val boundary = new Boundary(new Point(0.0, 0.0), new Point(screenWidth, screenHeight))
            val tree = new QuadTree(boundary)
            bodies.foreach(tree.insert)

            // Ensure all updates are done before moving on
            val futures = bodies.map { body =>
                Future {
                    val (fx, fy) = body.calculateForce(tree, THETA, EPSILON)
                    body.updatePhysics(elapsed, fx, fy)
                }
            }

            // ensuring parallel finishes before returning
            Await.result(Future.sequence(futures), Duration.Inf)

            counter += 1
            lastTime = System.nanoTime()
        }

        val totalTime = (System.nanoTime() - startTime) / 1e9
        totalTime
    }
}
