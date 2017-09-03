package actors

import akka.actor.Actor

class InfluxDbActor extends Actor {

  import InfluxDbActor._

  override def receive: Receive = {
    case Measurement(timestamp, temperature, humidity) =>
      println("writing " + temperature)
  }
}

object InfluxDbActor {
  case class Measurement(timestamp: Int, temperature: Float, humidity: Float)
}
