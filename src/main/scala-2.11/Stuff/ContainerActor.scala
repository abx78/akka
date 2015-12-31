package Stuff

import akka.actor.{ActorRef, Props, Actor}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.{Future, ExecutionContext}

class MainActor(lazyActor:ActorRef) extends Actor {
  implicit val timeout = Timeout(80 seconds)
  implicit val ec:ExecutionContext = context.dispatcher

  def receive ={
    case message : LazyMessage => {

      val future1 = lazyActor ? message
      val future2 = lazyActor ? message
      val future3 = lazyActor ? message

      val result = for{
        x <- future1
        y <- future2
        z <- future3
      } yield (x, y, z)


      result onComplete (res=>
        sender!res
        )
    }
  }
}



