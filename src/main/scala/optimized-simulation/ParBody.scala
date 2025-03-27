package optimizedsimulation

import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scala.collection.mutable.ListBuffer
import scalafx.scene.input.KeyCode.Minus
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

class ParBody(var x: Double, var y: Double, var mass: Double, val radius: Double, val G: Double) {

    val vel = Array(0.0, 0.0)
    val acc = Array(0.0, 0.0)
    val fc = Array(0.0, 0.0)

    val parBody = Circle(x, y, radius, Color.White)

    def seqCalculateForce(tree: QuadTree, theta: Double, epsilon: Double): (Double, Double) = {
            
            // if no body or no quadrants, return default
            if (tree.body.isEmpty && tree.topLeftTree == null ) { (0.0, 0.0) }
    
            // calculate d
            val dx = tree.comX - this.x
            val dy = tree.comY - this.y
            var d = math.sqrt(dx * dx + dy * dy + epsilon * epsilon)  // prevents division by 0
    
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
                var totalX = 0.0
                var totalY = 0.0
                if (tree.topLeftTree != null) {
                    val (fxx, fyy) = seqCalculateForce(tree.topLeftTree, theta, epsilon)
                    totalX += fxx
                    totalY += fyy
                }
                if (tree.topRightTree != null) {
                    val (fxx, fyy) = seqCalculateForce(tree.topRightTree, theta, epsilon)
                    totalX += fxx
                    totalY += fyy
                }
                if (tree.bottomLeftTree != null) {
                    val (fxx, fyy) = seqCalculateForce(tree.bottomLeftTree, theta, epsilon)
                    totalX += fxx
                    totalY += fyy
                }
                if (tree.bottomRightTree != null) {
                    val (fxx, fyy) = seqCalculateForce(tree.bottomRightTree, theta, epsilon)
                    totalX += fxx
                    totalY += fyy
                }
                (totalX, totalY)
            }
    }

    def parCalculateForce(tree: QuadTree, theta: Double, epsilon: Double): (Double, Double) = {

        // if no body or no quadrants, return default
        if (tree.body.isEmpty && tree.topLeftTree == null) { (0.0, 0.0) }

        if (tree.depth >= 1) {
            return seqCalculateForce(tree, theta, epsilon)
        }

        // calculate d
        val dx = tree.comX - this.x
        val dy = tree.comY - this.y
        var d = math.sqrt(dx * dx + dy * dy + epsilon * epsilon)  // prevents division by 0

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
            val futures = List(
            if (tree.topLeftTree != null) Future(parCalculateForce(tree.topLeftTree, theta, epsilon)) else Future.successful((0.0, 0.0)),
            if (tree.topRightTree != null) Future(parCalculateForce(tree.topRightTree, theta, epsilon)) else Future.successful((0.0, 0.0)),
            if (tree.bottomLeftTree != null) Future(parCalculateForce(tree.bottomLeftTree, theta, epsilon)) else Future.successful((0.0, 0.0)),
            if (tree.bottomRightTree != null) Future(parCalculateForce(tree.bottomRightTree, theta, epsilon)) else Future.successful((0.0, 0.0))
            )

            val results = futures.map(Await.result(_, Duration.Inf))
            val totalX = results.iterator.map(_._1).sum
            val totalY = results.iterator.map(_._2).sum
            (totalX, totalY)
        }
    }

    def update(time: Double, tree: QuadTree, theta: Double, epsilon: Double): Unit = {
    
        // uses the barnes hut implementation to calculate the force acting on this body
        val (newFx, newFy) = this.parCalculateForce(tree, theta, epsilon)
        fc(0) = newFx
        fc(1) = newFy

        // update acceleration
        acc(0) = fc(0) / mass
        acc(1) = fc(1) / mass

        // update velocity
        vel(0) += acc(0) * time
        vel(1) += acc(1) * time

        // update position
        x += vel(0) * time
        y += vel(1) * time
        parBody.centerX() = x
        parBody.centerY() = y
    }
}
