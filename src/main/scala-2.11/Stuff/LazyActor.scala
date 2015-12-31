package Stuff

import akka.actor.{Props, Actor}
import akka.event.Logging
import scala.concurrent.duration._

class LazyActor() extends Actor {

  var state:Int = 0

  def receive = {
    case msg : LazyMessage => {
      state+=1
      sender!s"${msg.context}-${msg.content} processed, state is $state "
    }
  }
}