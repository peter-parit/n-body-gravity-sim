import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scala.collection.mutable.ListBuffer
import scalafx.scene.input.KeyCode.Minus
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

class ParBody(var x: Double, var y: Double, var mass: Double, val radius: Double) {

    var vx: Double = 0.0
    var vy: Double = 0.0
    var ax: Double = 0.0
    var ay: Double = 0.0

    var fx: Double = 0.0
    var fy: Double = 0.0

    var G: Double = 0.1 // fake gravitational constant

    val parBody = Circle(x, y, radius, Color.White)

    def calculateForce(tree: QuadTree, theta: Double, epsilon: Double): (Double, Double) = {

        // if no body or no quadrants, return default
        if (tree.body.isEmpty && tree.topLeftTree == null) { (0.0, 0.0) }

        // calculate d
        val dx = tree.comX - this.x
        val dy = tree.comY - this.y
        var d = math.sqrt(dx * dx + dy * dy) + epsilon * epsilon // prevents division by 0

        // if ratio s / d is in the threshold theta, calculate force
        val s = tree.boundary.bottomRight.x - tree.boundary.topLeft.x
        if (s / d < theta) {
            val F = (G * this.mass * tree.totalMass) / (d * d)

            val Fx = F * (dx / d)
            val Fy = F * (dy / d)
            (Fx, Fy)
        } 
        
        // otherwise, recursion on children
        else {
            val futures = Seq(
                Future { if (tree.topLeftTree != null) then calculateForce(tree.topLeftTree, theta, epsilon) else (0.0, 0.0) },
                Future { if (tree.topRightTree != null) then calculateForce(tree.topRightTree, theta, epsilon) else (0.0, 0.0) },
                Future { if (tree.bottomLeftTree != null) then calculateForce(tree.bottomLeftTree, theta, epsilon) else (0.0, 0.0) },
                Future { if (tree.bottomRightTree != null) then calculateForce(tree.bottomRightTree, theta, epsilon) else (0.0, 0.0) }
            )

            // combine all forces
            val results = Await.result(Future.sequence(futures), Duration.Inf)
            results.reduce((Fx, Fy) => (Fx._1 + Fy._1, Fx._2 + Fy._2))
        }
    }

    def update(time: Double, tree: QuadTree, theta: Double, epsilon: Double): Unit = {
    
        // uses the barnes hut implementation to calculate the force acting on this body
        val (newFx, newFy) = this.calculateForce(tree, theta, epsilon)
        fx = newFx
        fy = newFx

        // update acceleration
        ax = fx / mass
        ay = fy / mass

        // update velocity
        vx += ax * time
        vy += ay * time

        // update position
        x += vx * time
        y += vy * time
        parBody.centerX() = x
        parBody.centerY() = y
    }
}
