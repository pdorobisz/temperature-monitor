package actors

import javax.inject.Inject

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import models.SensorReading
import play.api.Logger
import views.SensorReadingView

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class WebSocketActor @Inject()(id: String, cacheActor: ActorRef, out: ActorRef)(implicit ec: ExecutionContext) extends Actor {

  import WebSocketActor._

  private implicit val timeout: Timeout = 5.seconds
  private var latestTimestamp: Long = 0

  override def preStart: Unit = {
    context.system.eventStream.subscribe(self, classOf[SensorReading])
    Logger.debug(s"webSocket actor $id started")
  }

  override def postStop(): Unit = {
    Logger.debug(s"webSocket actor $id stopped")
    super.postStop()
  }

  override def receive: Receive = {
    case r: SensorReading => sendToWebSocket(r)
    case ClientCommand("GET_LATEST_READINGS") => cacheActor ! MeasurementCacheActor.GetReading
    case ClientCommand(c) => Logger.warn(s"received unknown command: $c")
  }

  private def sendToWebSocket(r: SensorReading) =
    if (r.timestamp > latestTimestamp) {
      latestTimestamp = r.timestamp
      out ! SensorReadingView(r)
    }
}

object WebSocketActor {

  def props(id: String, cacheActor: ActorRef, out: ActorRef, ec: ExecutionContext) = Props(new WebSocketActor(id, cacheActor, out)(ec))

  case class ClientCommand(command: String)

}
