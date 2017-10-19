package actors

import javax.inject.Inject

import actors.SensorActor.{Measurement, Read}
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import play.api.Logger

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class WebSocketActor @Inject()(id: String, sensorActor: ActorRef, out: ActorRef)(implicit ec: ExecutionContext) extends Actor {

  private implicit val timeout: Timeout = 5.seconds

  override def preStart: Unit = {
    context.system.eventStream.subscribe(self, classOf[Measurement])
    Logger.debug(s"webSocket actor $id started")
  }

  override def postStop(): Unit = {
    Logger.debug(s"webSocket actor $id stopped")
    super.postStop()
  }

  override def receive: Receive = {
    case m: Measurement =>
      out ! m
    case WebSocketActor.ClientCommand("GET_LATEST_MEASUREMENT") =>
      val future: Future[Option[Measurement]] = (sensorActor ? Read).mapTo[Option[Measurement]]
      future.map(_.get) pipeTo out
    case WebSocketActor.ClientCommand(c) =>
      Logger.warn(s"unknown command: $c")
  }
}

object WebSocketActor {

  def props(id: String, sensorActor: ActorRef, out: ActorRef, ec: ExecutionContext) = Props(new WebSocketActor(id, sensorActor, out)(ec))

  case class ClientCommand(command: String)

}
