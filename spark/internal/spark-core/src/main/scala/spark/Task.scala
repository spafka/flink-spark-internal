package spark

import java.io.Serializable

trait Task[T] extends Serializable {
  def run: T

  def preferredLocations: Seq[String] = Nil

  def markStarted(offer: SlaveOffer) {}
}

class FunctionTask[T](body: () => T) extends Task[T] with Serializable {
  def run: T = body()
}
