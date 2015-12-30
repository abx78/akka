package Part1.Intro

import Stuff.ReplyActor
import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._

object _2Ask extends App{

  implicit val timeout = Timeout(500 milli)

  val system = ActorSystem("demo")
  val actorAsk = system.actorOf(Props[ReplyActor], "askActor")
  val future = actorAsk ? "Ask OK"
  val result = Await.result(future, timeout.duration)

  println(s"Ask example executed successfully :" + result)
  system.terminate()
}
