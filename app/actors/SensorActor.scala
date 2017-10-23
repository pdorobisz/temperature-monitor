package actors

import javax.inject.Inject

import akka.actor.Actor
import models.SensorReading
import play.api.{Configuration, Logger}

import scala.util.{Failure, Success, Try}

class SensorActor @Inject()(config: Configuration) extends Actor {

  import SensorActor._

  import sys.process._

  private var lastReading: Option[SensorReading] = None
  private val sensorCommand = "%s%s %s".format(
    config.getOptional[String]("app.home").map(_ + "/bin/").getOrElse(""),
    config.get[String]("app.sensor.command"),
    config.getOptional[String]("app.sensor.pin").getOrElse(""))

  override def receive: Receive = {
    case Tick =>
      readSensor().foreach { currentReading =>
        lastReading = Some(currentReading)
        context.system.eventStream.publish(currentReading)
      }
    case Read => sender ! lastReading
  }

  private def readSensor(): Option[SensorReading] = Try(sensorCommand.!!) match {
    case Failure(e) =>
      Logger.error(s"failed to execute sensor command: $sensorCommand", e)
      None
    case Success(value) if value.isEmpty =>
      Logger.warn("sensor not available")
      None
    case Success(value) => Try {
      val tmp :: hum :: Nil = value.trim().split(' ').toList
      val r = SensorReading(System.currentTimeMillis(), tmp.toFloat, hum.toFloat)
      Logger.info(s"read from sensor: $r")
      Some(r)
    }.recoverWith { case _ =>
      Logger.error(s"sensor command returned invalid response: $value")
      Success(None)
    }.get
  }
}


object SensorActor {

  case object Tick

  case object Read

}
