package actors

import actors.SensorActor.Measurement
import akka.actor.{Actor, ActorRef, Props}
import play.api.Logger

class WebSocketActor(out: ActorRef) extends Actor {

  override def preStart: Unit = {
    context.system.eventStream.subscribe(self, classOf[Measurement])
    Logger.debug("webSocket actor started")
  }

  override def postStop(): Unit = {
    Logger.debug("webSocket actor stopped")
    super.postStop()
  }

  override def receive: Receive = {
    case m: Measurement =>
      out ! m
    case m: String =>
      println("got: " + m)
      out ! s"$m DONE"
  }
}

object WebSocketActor {
  def props(out: ActorRef) = Props(new WebSocketActor(out))
}
