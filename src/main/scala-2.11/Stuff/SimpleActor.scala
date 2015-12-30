package Stuff

import akka.actor.Actor
import akka.event.Logging

class SimpleActor extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case msg : String =>
      println (s"message $msg received")
  }
}