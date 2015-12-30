package Stuff

import akka.actor.{Props, Actor}
import akka.event.Logging
import scala.concurrent.duration._

class LazyActor(sleep:Int) extends Actor {
  import context._
  val log = Logging(context.system, this)

  var state:Int = 0

  def receive = {
    case msg : String => {
      log.info(s"message $msg received")
      state+=1
      Thread.sleep(sleep)
      sender!s"message $msg processed after $sleep state is $state "
 
    }
  }
}