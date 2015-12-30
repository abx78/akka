package Part1.Intro

import Stuff.SimpleActor
import akka.actor.{ActorSystem, Props}

object _1Tell extends App{
  val system = ActorSystem("demo")

  val actorTell = system.actorOf(Props[SimpleActor], "tellActor")
  actorTell ! "Tell OK"

  println("Tell example executed successfully")
  system.terminate()
}
