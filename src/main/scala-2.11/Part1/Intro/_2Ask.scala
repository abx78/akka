package Part1.Intro

import akka.actor.{ActorLogging, Actor, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask
import akka.pattern.pipe
import scala.concurrent.Await
import scala.concurrent.duration._

object _2Ask extends App{

  val system = ActorSystem("demo")
  val actorManager = system.actorOf(Props[ManagingActor])
  actorManager!"start"


  //system.terminate()
}

class ManagingActor extends Actor with ActorLogging {

  def receive = {
    case "start" =>
      val actorAsk = context.actorOf(Props[ReplyActor], "askActor")

      val future = (actorAsk ? "Ask OK")(500.milli).mapTo[String]
      val result = Await.result(future, 500.milli)

      log.info(s"Ask example executed successfully :" + result)

  }
}

class ReplyActor extends Actor with ActorLogging {

  def receive = {
    case msg : String =>
      log.info (s"message $msg received")
      sender ! "ok"
  }
}


// actor logging:
// Internally, when we log a message, the the logging methods in the ActorLogging
// publishes the log message to an EventStream
// EventStream behaves just like a message broker to which we could publish and receive messages.
// The distinction is that the subscribers of the EventStream could only be an Actor.
// By default the Actor that subscribes to these messages is the DefaultLogger which simply
// prints the message to the standard output.

/* configure with application.conf

akka{
loggers = ["akka.event.slf4j.Slf4jLogger"]
loglevel = "DEBUG"
logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"
}

*/

class ManagingActorNonBlocking extends Actor with ActorLogging {
  implicit val ec = context.dispatcher

  def receive = {
    case "start" =>
      val actorAsk = context.actorOf(Props[ReplyActor], "askActor")

      (actorAsk ? "Ask OK")(500.milli)
        .map {
          case Some(result) => sender ! result
          case None => sender ! "Empty result"
        }
        .recover { case _ => sender! "Error" }

      /*
      Calling sender in a Future during Actor.receive could return a null or be the wrong ActorRef


      In both success and failure callbacks we can't use sender directly because these pieces of
      code can be executed much later by another thread. sender's value is transient and by the
      time answer arrives it might point to any other actor that happened to send us something.
      Thus we have to keep original sender in origin local variable and capture that one instead.

      Simply use a val if you close over sender in a a block of code that might
      be executed by another thread.
      =>      val originalSender = sender
      */

  }
}

// option 2 -> pipeTo
class ManagingActorPipe extends Actor with ActorLogging {
  implicit val ec =context.dispatcher

  def receive = {
    case "start" =>
      val actorAsk = context.actorOf(Props[ReplyActor], "askActor")

      (actorAsk ? "Ask OK")(500.milli)
        .map {
          case Some(result) => result
          case None => "Empty result"
        }
        .recover { case _ => "Error" }
        .pipeTo(sender)
  }
}

