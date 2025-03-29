package optimizedsimulation

import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scala.collection.mutable.ListBuffer
import scalafx.scene.input.KeyCode.Minus
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.parallel.CollectionConverters._

class ParBody(var x: Double, var y: Double, var mass: Double, val radius: Double, val G: Double) {

    val vel = Array(0.0, 0.0)
    val acc = Array(0.0, 0.0)
    val fc = Array(0.0, 0.0)

    val parBody = Circle(x, y, radius, Color.White)

    // recursively calculate forces in each tree based on barnes-hut's approach
    def calculateForce(tree: QuadTree, theta: Double, epsilon: Double): (Double, Double) = {
            
        // if no body or no quadrants, return default
        if (tree.body.isEmpty && tree.topLeftTree == null ) { (0.0, 0.0) }

        // calculate d
        val dx = tree.comX - this.x
        val dy = tree.comY - this.y
        var d = math.sqrt(dx * dx + dy * dy + epsilon * epsilon)  // epsilon prevents division by 0

        // if ratio s / d is in the barnes-hut threshold theta, calculate force
        val s = tree.boundary.bottomRight.x - tree.boundary.topLeft.x
        if (s / d < theta) {
            val F = (G * this.mass * tree.totalMass) / (d * d)

            val Fx = F * (dx / d)
            val Fy = F * (dy / d)
            (Fx, Fy)
        } 
        
        // otherwise, recursion on children
        else {
            var totalX = 0.0
            var totalY = 0.0
            if (tree.topLeftTree != null) {
                val (fxx, fyy) = calculateForce(tree.topLeftTree, theta, epsilon)
                totalX += fxx
                totalY += fyy
            }
            if (tree.topRightTree != null) {
                val (fxx, fyy) = calculateForce(tree.topRightTree, theta, epsilon)
                totalX += fxx
                totalY += fyy
            }
            if (tree.bottomLeftTree != null) {
                val (fxx, fyy) = calculateForce(tree.bottomLeftTree, theta, epsilon)
                totalX += fxx
                totalY += fyy
            }
            if (tree.bottomRightTree != null) {
                val (fxx, fyy) = calculateForce(tree.bottomRightTree, theta, epsilon)
                totalX += fxx
                totalY += fyy
            }
            
            (totalX, totalY)
        }
    }

    // update the physical attributes of the body
    def updatePhysics(time: Double, fx: Double, fy: Double): Unit = {

        // force
        fc(0) = fx
        fc(1) = fy
        
        // acceleration
        acc(0) = fc(0)/mass
        acc(1) = fc(1)/mass

        // velocity
        vel(0) += acc(0)*time
        vel(1) += acc(1)*time

        // position (the position on the screen will be updated in the simulation)
        x += vel(0)*time
        y += vel(1)*time
    }
}
