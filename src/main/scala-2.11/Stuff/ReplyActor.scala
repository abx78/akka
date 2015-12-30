package Stuff

import akka.actor.Actor
import akka.event.Logging

/**
  * Created by ABX on 12/30/2015.
  */
class ReplyActor extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case msg : String =>
      println (s"message $msg received")
      sender ! "ok"
  }
}
