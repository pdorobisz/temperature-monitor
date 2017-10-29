package controllers

import javax.inject._

import actors.WebSocketActor.ClientCommand
import actors.{MeasurementCacheActor, WebSocketActor}
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import models.SensorReading
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import streams.ActorFlow
import views.SensorReadingView

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class HomeController @Inject()(cc: ControllerComponents, @Named("measurement-cache-actor") cacheActor: ActorRef)
                              (implicit ec: ExecutionContext, system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {

  private implicit val inputFormat = Json.format[ClientCommand]
  private implicit val outputFormat = Json.format[SensorReadingView]
  private implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[ClientCommand, SensorReadingView]
  private implicit val timeout: Timeout = 5.seconds

  def index: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    (cacheActor ? MeasurementCacheActor.LastReading).mapTo[Option[SensorReading]].map { m =>
      val r = m.map(SensorReadingView.apply)
      Ok(views.html.index(r))
    }.recover {
      case e =>
        Logger.error(s"failed to get latest readings", e)
        Ok(views.html.index(None))
    }
  }

  def ws: WebSocket = WebSocket.accept[ClientCommand, SensorReadingView] { request =>
    ActorFlow.actorRef { out =>
      WebSocketActor.props(request.id.toString, cacheActor, out, ec)
    }
  }
}
