package actors

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef}

class SensorActor @Inject()(@Named("influxdb-actor") influxDbActor: ActorRef) extends Actor {

  import SensorActor._

  private var lastReading: Option[Measurement] = None

  override def receive: Receive = {
    case Tick =>
      println("reading data from sensor")
      influxDbActor ! InfluxDbActor.Measurement(1, 12.13f, 0.67f)

    case Read => lastReading
  }
}

object SensorActor {

  case object Tick

  case object Read

  case class Measurement(timestamp: Int, temperature: Float, humidity: Float)

}
