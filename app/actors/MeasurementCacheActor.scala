package actors

import akka.actor.Actor
import models.SensorReading

class MeasurementCacheActor extends Actor {

  import MeasurementCacheActor._

  private var lastReading: Option[SensorReading] = None

  override def preStart: Unit = {
    context.system.eventStream.subscribe(self, classOf[SensorReading])
  }

  override def receive: Receive = {
    case r: SensorReading => lastReading = Some(r)
    case LastReading => sender() ! lastReading
    case GetReading => lastReading.foreach(sender() ! _)
  }
}

object MeasurementCacheActor {

  case object LastReading

  case object GetReading

}
