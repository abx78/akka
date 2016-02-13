package Part2.Refactor

import Stuff._
import akka.actor.{ActorRef, Actor, Props, ActorSystem}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object _2ExtraPattern_Ask extends App {

  val system = ActorSystem("extraPatternSystem")
  implicit val timeout = Timeout(80 seconds)
  implicit val ec:ExecutionContext = system.dispatcher

  val worker1 = system.actorOf(Props[Worker], "firstWorker")
  val orchestrator = system.actorOf(Props(classOf[Orchestrator], worker1), "OrchestratorActor")

  orchestrator ! new SimpleMessage("Start")


  class Orchestrator(worker:ActorRef) extends Actor{

    def receive = {
      case result:Result =>
        println(result)
      case _=>

        val fut1 = worker ? new DoSomeWork("do some work")
        val fut2 = worker ? new DoMoreWork("more work")
        val fut3 = worker ? new DoEvenMore("do additional work")

        val futResult = for{
          w1 <- fut1.mapTo[MessageBase]
          w2 <- fut2.mapTo[MessageBase]
          w3 <- fut3.mapTo[MessageBase]
        } yield Result(w1.content, w2.content, w3.content)

        // why not ask?
        /*
        There are performance implications of using ask since something needs to keep track of when it times out,
        there needs to be something that bridges a Promise into an ActorRef and it also needs to be reachable through remoting.
        So always prefer tell for performance, and only ask if you must.

        In all these methods you have the option of passing along your own ActorRef.
        Make it a practice of doing so because it will allow the receiver actors to be able to respond to your message,
        since the sender reference is sent along with the message.

        http://doc.akka.io/docs/akka/2.4.1/java/untyped-actors.html#Send_messages

        */

        futResult map (myRes=>self!myRes) // sender might be different from when the future has started!!!
    }
  }
}

// EXTRA PATTERN

object _2ExtraPattern_Extra extends App {

  val system = ActorSystem("extraPatternSystem")
  implicit val timeout = Timeout(80 seconds)
  implicit val ec:ExecutionContext = system.dispatcher

  val worker1 = system.actorOf(Props[Worker], "firstWorker")
  val orchestrator = system.actorOf(Props(classOf[Orchestrator], worker1), "OrchestratorActor")

  orchestrator ! new SimpleMessage("Start")

  class Orchestrator(worker:ActorRef) extends Actor{

    def receive = {
      case result:Result => println("Orchestrator received: " + result)
      case start:SimpleMessage =>
        val original = self
        var res1, res2, res3: Option[String] = None

        context.actorOf(Props(new Actor {

          worker!DoSomeWork("go")
          worker!DoMoreWork("go")
          worker!DoEvenMore("go")

          val timeoutMessenger = context.system.scheduler.scheduleOnce(2 seconds){
            self!"timeout!"
          }

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
          original!response
          context.stop(self)
        }
    }
        ))
    }
  }
}

class Worker extends Actor {
  def receive = {
    case msg:MessageBase =>
      sender!msg
  }
}