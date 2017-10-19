package controllers

import javax.inject._

import actors.SensorActor.Measurement
import actors.WebSocketActor.ClientCommand
import actors.{SensorActor, WebSocketActor}
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import streams.ActorFlow

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class HomeController @Inject()(cc: ControllerComponents, @Named("sensor-actor") sensorActor: ActorRef)
                              (implicit ec: ExecutionContext, system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {

  private implicit val inEventFormat = Json.format[ClientCommand]
  private implicit val outEventFormat = Json.format[Measurement]
  private implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[ClientCommand, Measurement]
  private implicit val timeout: Timeout = 5.seconds

  def index: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    (sensorActor ? SensorActor.Read).mapTo[Option[Measurement]].map { m =>
      val s = m.map(m => s"time: ${m.timestamp}, temperature: ${m.temperature}, humidity: ${m.humidity}").getOrElse("data not available yet")
      Ok(views.html.index(s))
    }
  }

  def ws: WebSocket = WebSocket.accept[ClientCommand, Measurement] { request =>
    ActorFlow.actorRef { out =>
      WebSocketActor.props(request.id.toString, sensorActor, out, ec)
    }
  }
}
