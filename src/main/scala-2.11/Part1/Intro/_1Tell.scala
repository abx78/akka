package Part1.Intro

import akka.actor.{Actor, ActorSystem, Props}

object _1Tell extends App{
  val system = ActorSystem("demo")

  val actorTell = system.actorOf(Props[SimpleActor], "tellActor")
  actorTell ! "Tell OK"

  println("Tell example executed successfully")
  //system.terminate()
}


class SimpleActor extends Actor {

  def receive = {
    case msg : String =>
      println (s"message $msg received")
  }
}