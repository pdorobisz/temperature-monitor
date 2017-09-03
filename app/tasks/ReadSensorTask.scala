package tasks

import javax.inject.{Inject, Named}

import actors.SensorActor
import akka.actor.{ActorRef, ActorSystem}
import play.api.Configuration

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ReadSensorTask @Inject()(configuration: Configuration, actorSystem: ActorSystem, @Named("sensor-actor") sensorActor: ActorRef)(implicit executionContext: ExecutionContext) {

  actorSystem.scheduler.schedule(
    initialDelay = 0.microseconds,
    interval = configuration.get[Int]("app.sensor.interval").seconds,
    receiver = sensorActor,
    message = SensorActor.Tick
  )


}
