package actors

import javax.inject.Inject

import akka.actor.Actor
import play.api.{Configuration, Logger}

import scala.util.{Failure, Success, Try}

class SensorActor @Inject()(config: Configuration) extends Actor {

  import SensorActor._

  import sys.process._

  private var lastReading: Option[Measurement] = None
  private val sensorCommand = config.get[String]("app.sensor.command")

  override def receive: Receive = {
    case Tick =>
      readSensor().foreach { measurement =>
        lastReading = Some(measurement)
        context.system.eventStream.publish(measurement)
      }
    case Read => sender ! lastReading
  }

  private def readSensor(): Option[Measurement] = Try(sensorCommand.!!) match {
    case Failure(e) =>
      Logger.error(s"failed to execute sensor command: $sensorCommand", e)
      None
    case Success(value) if value.isEmpty =>
      Logger.warn("sensor not available")
      None
    case Success(value) => Try {
      val tmp :: hum :: Nil = value.split(' ').toList
      val m = Measurement(System.currentTimeMillis(), tmp.toFloat, hum.toFloat)
      Logger.debug(s"read from sensor: $m")
      Some(m)
    }.recoverWith { case _ =>
      Logger.error(s"sensor command returned invalid response: $value")
      Success(None)
    }.get
  }
}

object SensorActor {

  case object Tick

  case object Read

  case class Measurement(timestamp: Long, temperature: Float, humidity: Float)

}
