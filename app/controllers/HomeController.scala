package controllers

import javax.inject._

import actors.SensorActor
import actors.SensorActor.Measurement
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class HomeController @Inject()(cc: ControllerComponents, @Named("sensor-actor") sensorActor: ActorRef)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  implicit val timeout: Timeout = 5.seconds

  def index = Action.async {
    (sensorActor ? SensorActor.Read).mapTo[Option[Measurement]].map { m =>
      val s = m.map(m => s"time: ${m.timestamp}, temperature: ${m.temperature}, humidity: ${m.humidity}").getOrElse("data not available yet")
      Ok(views.html.index(s))
    }
  }
}
