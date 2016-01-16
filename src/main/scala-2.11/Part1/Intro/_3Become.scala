package Part1.Intro

import akka.actor.{ Actor, ActorSystem, Props}

object _3Become extends App{

  val system = ActorSystem("stateSystem")
  val actor = system.actorOf(Props[StateActor], "aStateActor")

  actor!"bing"
  actor!"bing"
  actor!"bing"
  actor!"bing"
  actor!"bing"

}

class StateActor extends Actor {
  // hotswap of behavior
  def receive: Receive = {
    case "bing" => {
      println("I am normal")
      context become happy
    }
  }

  def happy: Receive = {
    case "bing" => {
      println("I am happy")
      context become happier
    }
  }

  def happier: Receive = {
    case "bing" => {
      println("I can't be happier than this now!")
      context unbecome
    }
  }
}

