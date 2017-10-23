package actors

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import models.{ReadingView, SensorReading}
import play.api.Logger

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class WebSocketActor @Inject()(id: String, out: ActorRef)(implicit ec: ExecutionContext) extends Actor {

  private implicit val timeout: Timeout = 5.seconds

  override def preStart: Unit = {
    context.system.eventStream.subscribe(self, classOf[SensorReading])
    Logger.debug(s"webSocket actor $id started")
  }

  override def postStop(): Unit = {
    Logger.debug(s"webSocket actor $id stopped")
    super.postStop()
  }

  override def receive: Receive = {
    case r: SensorReading => out ! ReadingView(r)
    case s: String => Logger.warn(s"received message: $s")
  }
}

object WebSocketActor {

  def props(id: String, out: ActorRef, ec: ExecutionContext) = Props(new WebSocketActor(id, out)(ec))
}
