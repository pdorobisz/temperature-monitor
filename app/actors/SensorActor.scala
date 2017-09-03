package actors

import akka.actor.Actor

class SensorActor extends Actor {

  import SensorActor._

  override def receive: Receive = {
    case Tick => println("tick")

    case Read => println("measurements")
  }
}

object SensorActor {

  case object Tick

  case object Read

  case class Measurements(s: String)

}
