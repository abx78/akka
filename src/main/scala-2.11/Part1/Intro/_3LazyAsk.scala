package Part1.Intro

import Stuff._
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

object _3LazyAsk extends App{

  val system = ActorSystem("demo")
  implicit val timeout = Timeout(80 seconds)
  implicit val ec:ExecutionContext = system.dispatcher

  val actorLazy = system.actorOf(Props[LazyActor], "askToALazyActor")

  val mainActor1 = system.actorOf(Props(new MainActor(actorLazy)), "mainActor1")
  val mainActor2 = system.actorOf(Props(new MainActor(actorLazy)), "mainActor2")

  var res1 = mainActor1 ? new LazyMessage("firstActor", "1")
  var res2 = mainActor2 ? new LazyMessage("secondActor", "2")

  val result = for{
    x <- res1
    y <- res2
  } yield (x, y)

  val res = Await.result(result, 80 seconds)
  println(res)

  system.terminate()

}

