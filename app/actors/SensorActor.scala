package actors

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef}

class SensorActor @Inject()(@Named("influxdb-actor") influxDbActor: ActorRef) extends Actor {

  import SensorActor._

  private var lastReading: Option[Measurement] = None

  override def receive: Receive = {
    case Tick =>
      println("reading data from sensor")
      val m = Measurement(System.currentTimeMillis(), 23.6f, 0.67f)
      lastReading = Some(m)
      influxDbActor ! InfluxDbActor.Measurement(m.timestamp, m.temperature, m.humidity)

    case Read => sender ! lastReading
  }
}

object SensorActor {

  case object Tick

  case object Read

  case class Measurement(timestamp: Long, temperature: Float, humidity: Float)

}
