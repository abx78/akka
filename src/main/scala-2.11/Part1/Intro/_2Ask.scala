package Part1.Intro

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._

object _2Ask extends App{

  implicit val timeout = Timeout(500 milli)

  val system = ActorSystem("demo")
  val actorAsk = system.actorOf(Props[ReplyActor], "askActor")

  val future = (actorAsk ? "Ask OK").mapTo[String]
  val result = Await.result(future, timeout.duration)

  println(s"Ask example executed successfully :" + result)
  system.terminate()
}

class ReplyActor extends Actor {

  def receive = {
    case msg : String =>
      println (s"message $msg received")
      sender ! "ok"
  }
}
