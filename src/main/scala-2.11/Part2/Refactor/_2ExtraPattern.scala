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
  val worker2 = system.actorOf(Props[Worker], "secondWorker")
  val worker3 = system.actorOf(Props[Worker], "thirdWorker")
  val orchestrator = system.actorOf(Props(classOf[Orchestrator], worker1, worker2, worker3), "OrchestratorActor")

  orchestrator ! new SimpleMessage("Start")

  class Orchestrator(worker1:ActorRef, worker2:ActorRef, worker3:ActorRef) extends Actor{

    def receive = {
      case _=>

        val fut1 = worker1 ? new SimpleMessage("do some work")
        val fut2 = worker2 ? new SimpleMessage("do more work")
        val fut3 = worker3 ? new SimpleMessage("do additional work")

        val futResult = for{
          w1 <- fut1.mapTo[String]
          w2 <- fut2.mapTo[String]
          w3 <- fut3.mapTo[String]
        } yield Result(w1, w2, w3)

        // why not ask?
        /*
        There are performance implications of using ask since something needs to keep track of when it times out,
        there needs to be something that bridges a Promise into an ActorRef and it also needs to be reachable through remoting.
        So always prefer tell for performance, and only ask if you must.

        In all these methods you have the option of passing along your own ActorRef.
        Make it a practice of doing so because it will allow the receiver actors to be able to respond to your message, since the sender reference is sent along with the message.

        http://doc.akka.io/docs/akka/2.4.1/java/untyped-actors.html#Send_messages

        */

        futResult map (myRes=> sender!myRes) // sender might be different from when the future has started!!!
    }
  }
}



// EXTRA PATTERN

object _2ExtraPattern_Extra extends App {

  val system = ActorSystem("extraPatternSystem")
  implicit val timeout = Timeout(80 seconds)
  implicit val ec:ExecutionContext = system.dispatcher

  val worker1 = system.actorOf(Props[Worker], "firstWorker")
  val worker2 = system.actorOf(Props[Worker], "secondWorker")
  val worker3 = system.actorOf(Props[Worker], "thirdWorker")
  val orchestrator = system.actorOf(Props(classOf[Orchestrator], worker1, worker2, worker3), "OrchestratorActor")

  orchestrator ! new SimpleMessage("Start")

  class Orchestrator(worker1:ActorRef, worker2:ActorRef, worker3:ActorRef) extends Actor{

    def receive = {
      case _ =>
        val original = sender
        var res1, res2, res3: Option[String] = None

        context.actorOf(Props(new Actor {

          def receive = {
            case msg:String =>
              if(msg == "timeout!"){
                sendResponse(msg)
              }
              else{
                if(res1 == None) {
                  res1 = Option(msg)
                } else{
                  if(res2 == None) {
                    res2 = Option(msg)
                  }else{
                    if(res3 == None) {
                      res3 = Option(msg)
                    }
                  }
                }
              }
              checkResults
          }

          def checkResults = (res1, res2, res3) match {
            case (Some(r1),Some(r2), Some(r3)) => {
              sendResponse(Result(res1.get, res2.get, res3.get))
            }
            case _ =>
          }

          def sendResponse(response:Any) = {
            original!response
          }

          worker1!SimpleMessage("go")
          worker2!SimpleMessage("go")
          worker3!SimpleMessage("go")

          val timeoutMessenger = context.system.scheduler.scheduleOnce(2 seconds){
            self!"timeout!"
          }
        }
        ))
    }
  }
}

class Worker extends Actor {
  def receive = {
    case msg:SimpleMessage => sender!msg.content
  }
}