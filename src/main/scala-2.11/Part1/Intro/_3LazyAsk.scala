package Part1.Intro

import Stuff._
import akka.actor.{ActorSystem, Props}

object _3LazyAsk extends App{

  val system = ActorSystem("demo")
  val containerActor = system.actorOf(Props[ContainerActor], "ContainerActor")
  containerActor ! "start"
  containerActor ! "again"

  system.terminate()


}

