package naivesimulation

import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scala.collection.mutable.ListBuffer

class NaiveBody(var x: Double, var y: Double, var mass: Double, val radius: Double, val G: Double) {

    var vx: Double = 0.0
    var vy: Double = 0.0
    var ax: Double = 0.0
    var ay: Double = 0.0

    var fx: Double = 0.0
    var fy: Double = 0.0

    val naiveBody = Circle(x, y, radius, Color.White)

    def calculateForce(other: NaiveBody, epsilon: Double): (Double, Double) = {
        val dx = other.x - this.x
        val dy = other.y - this.y
        var r = math.sqrt(dx * dx + dy * dy + epsilon * epsilon)

        val F = (G * this.mass * other.mass) / math.pow(r, 2)

        val Fx = F * (dx / r)
        val Fy = F * (dy / r)

        (Fx, Fy)
    }

    def update(time: Double, bodies: ListBuffer[NaiveBody], epsilon: Double): Unit = {
        fx = 0
        fy = 0

        for (other <- bodies if other != this) {
            val (fx, fy) = calculateForce(other, epsilon)
            this.fx += fx
            this.fy += fy
        }

        ax = fx / mass
        ay = fy / mass

        vx += ax * time
        vy += ay * time

        x += vx * time
        y += vy * time

        naiveBody.centerX() = x
        naiveBody.centerY() = y
    }
}
