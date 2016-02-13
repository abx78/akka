package Part2.Refactor

import Stuff.{Result, DoEvenMore, DoMoreWork, DoSomeWork}
import akka.actor.{Props, ActorRef, Actor, ActorLogging}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object _3CameoPattern extends App {

}

object ResponseHandler  {
  case object Timeout
  def props (worker: ActorRef, originalSender:ActorRef) : Props = {
    Props(new ResponseHandler(worker, originalSender))
  }
}

class ResponseHandler(worker: ActorRef, originalSender:ActorRef) extends Actor with ActorLogging {

  implicit val ec:ExecutionContext = context.dispatcher
  var res1, res2, res3: Option[String] = None


  def receive = {

    case "timeout!" =>
      sendResponse("timeout!")

    case msg:DoSomeWork =>
      res1 = Option(msg.content)
      checkResults

    case msg:DoMoreWork =>
      res2 = Option(msg.content)
      checkResults

    case msg:DoEvenMore =>
      res3 = Option(msg.content)
      checkResults
  }

  def checkResults = (res1, res2, res3) match {
    case (Some(r1),Some(r2), Some(r3)) => {
      sendResponse(Result(res1.get, res2.get, res3.get))
    }
    case _ =>
  }

  def sendResponse(response:Any) = {
    println(response)
    originalSender!response
    context.stop(self)
  }

  worker!DoSomeWork("go")
  worker!DoMoreWork("go")
  worker!DoEvenMore("go")

  val timeoutMessenger = context.system.scheduler.scheduleOnce(2 seconds){
    self!"timeout!"
  }

}