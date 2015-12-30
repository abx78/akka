package Stuff

import akka.actor.{Props, Actor}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.{Await, ExecutionContext}

class ContainerActor extends Actor {
  implicit val timeout = Timeout(8 seconds)
  implicit val ec:ExecutionContext = context.dispatcher

  val actorLazy1 = context.actorOf(Props(new LazyActor(1000)), "askToALazyActor1")
  val actorLazy2 = context.actorOf(Props(new LazyActor(500)), "askToALazyActor2")
  val actorLazy3 = context.actorOf(Props(new LazyActor(200)), "askToALazyActor3")

  def receive ={
    case "start" => {

      val future1 = actorLazy1 ? "Lazy 1"
      val future2 = actorLazy2 ? "Lazy 2"
      val future3 = actorLazy3 ? "Lazy 3"

      val result = for{
        x <- future1
        y <- future2
        z <- future3
      } yield Result(x, y, z)

      result map {
        x => println("Lazy Ask example executed successfully :" + x)
      }
    }
    case "again" =>{
      val future1 = actorLazy1 ? "Lazy 1"

      future1 map {
        x => println("Lazy Ask example executed again successfully :" + x)
      }

    }
  }
}



