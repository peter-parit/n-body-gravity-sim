import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scala.collection.mutable.ListBuffer
import scalafx.scene.input.KeyCode.Minus
class ParBody(var x: Double, var y: Double, var mass: Double, val radius: Double) {

    var vx: Double = 0.0
    var vy: Double = 0.0
    var ax: Double = 0.0
    var ay: Double = 0.0

    var fx: Double = 0.0
    var fy: Double = 0.0

    var G: Double = 0.1 // fake gravitational constant

    val parBody = Circle(x, y, radius, Color.White)

    def calculateForce(other: ParBody): (Double, Double) = {
        val dx = other.x - this.x
        val dy = other.y - this.y
        var r = math.sqrt(dx * dx + dy * dy)

        // avoid r that is too small
        val MIN_R = (this.radius + other.radius) * 2
        if (r < MIN_R) then r = MIN_R

        val F = (G * this.mass * other.mass) / math.pow(r, 2)

        val Fx = F * (dx / r)
        val Fy = F * (dy / r)

        (Fx, Fy)
    }

    def update(time: Double, bodies: ListBuffer[ParBody]): Unit = {
        fx = 0
        fy = 0

        // TODO: Use barnes hut algorithm -> make parallel.
        for (other <- bodies if other != this) {
            val (fx, fy) = calculateForce(other)
            this.fx += fx
            this.fy += fy
        }

        ax = fx / mass
        ay = fy / mass

        vx += ax * time
        vy += ay * time

        x += vx * time
        y += vy * time

        parBody.centerX() = x
        parBody.centerY() = y
    }
}
