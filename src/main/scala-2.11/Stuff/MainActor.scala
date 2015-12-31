package Stuff

import akka.actor.Actor.Receive
import akka.actor.{ActorRef, Props, Actor}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
/*
import scala.concurrent.{ ExecutionContext}

class MainActor(lazyActor:ActorRef) extends Actor {
  def receive = {
    val future1 = lazyActor ? "Lazy 4"

  }
}
*/