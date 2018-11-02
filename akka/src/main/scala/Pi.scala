import akka.actor._
import akka.routing.{RoundRobinPool, Router}
import scala.concurrent.duration._

object Pi extends App {
  // based on http://doc.akka.io/docs/akka/2.0/intro/getting-started-first-scala.html
  sealed trait PiMessage
  case object Calculate extends PiMessage
  case class Work(limit: Int, offset: Int) extends PiMessage
  case class Result(value: Double) extends PiMessage
  case class PiApproximation(pi: Double, duration: Duration)

  class Worker extends Actor {
    def receive = {
      case Work(limit, offset) =>
        sender ! Result(calculatePiFor(limit, offset))
    }

    def calculatePiFor(limit: Int, offset: Int): Double = {
      (offset until (limit + offset))
        .map(i => 4.0 * (1 - (i % 2) * 2) / (2 * i + 1))
        .sum
    }
  }

  class Master(numWorkers: Int, numMessages: Int, numElements: Int, listener: ActorRef) extends Actor {
    var pi: Double = _
    var numResults: Int = _
    val start: Long = System.currentTimeMillis

    val workerRouter = context.actorOf(
      RoundRobinPool(numWorkers).props(Props[Worker]),
      name = "workerRouter"
    )

    def receive = {
      case Calculate => (0 until numMessages)
        .foreach( i => workerRouter ! Work(numElements, i * numElements))
      case Result(value) =>
        pi += value
        numResults += 1
        if (numResults == numMessages) {
          listener ! PiApproximation(pi, duration=(System.currentTimeMillis - start).millis)
          context.stop(self)
        }

    }
  }

  class Listener extends Actor {
    def receive = {
      case PiApproximation(pi, duration) =>
        println("\n\tPi approximation: \t\t%s\n\tCalculation time: \t%s"
          .format(pi, duration))
        context.system.shutdown
    }
  }

  def calculate(numWorkers: Int, numElements: Int, numMessages: Int) {
    val system = ActorSystem("PiSystem")
    val listener = system.actorOf(Props[Listener], name="listener")
    val master = system.actorOf(Props(new Master(numWorkers, numElements, numMessages, listener)), name="master")
    master ! Calculate
  }

  calculate(numWorkers = 6, numElements = 10000, numMessages = 40000)
}