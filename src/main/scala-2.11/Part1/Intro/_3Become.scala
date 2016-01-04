package Part1.Intro

import akka.actor.{ActorRef, Actor, ActorSystem, Props}

object _3Become extends App{

  val system = ActorSystem("stateSystem")
  val actor = system.actorOf(Props[StateActor], "aStateActor")

  actor.tell("bing", ActorRef.noSender)
  actor.tell("bing", ActorRef.noSender)
  actor.tell("bing", ActorRef.noSender)
  actor.tell("bing", ActorRef.noSender)
  actor.tell("bing", ActorRef.noSender)
  actor.tell("bing", ActorRef.noSender)
}

class StateActor extends Actor {
  // hotswap of behavior
  def receive: Receive = {
    case _ => {
      println("I am normal")
      context become happy
    }
  }

  def happy: Receive = {
    case _ => {
      println("I am happy")
      context become happier
    }
  }

  def happier: Receive = {
    case _ => {
      println("I can't be happier than this now!")
      context unbecome
    }
  }
}

