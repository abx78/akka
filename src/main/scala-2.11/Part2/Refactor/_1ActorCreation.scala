package Part2.Refactor

import Stuff.SimpleMessage
import akka.actor.{ActorSystem, Props, Actor}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.{Await, ExecutionContext}
import scala.util.Random


object _1ActorCreation extends App{

  val system = ActorSystem("actorCreationSystem")
  implicit val timeout = Timeout(80 seconds)
  implicit val ec:ExecutionContext = system.dispatcher

  val outerActor = system.actorOf(Props(new OuterActor()), "OuterActor1")
  outerActor ! new SimpleMessage("A message")

  class OuterActor extends Actor {

    implicit val timeout = Timeout(80 seconds)

    val innerActor = context.actorOf(Props(new InnerActor), "innerActor")

    def receive = {
      case msg: SimpleMessage =>{
        innerActor ! msg
      }
    }
  }

  class InnerActor() extends Actor {
    implicit val timeout = Timeout(120 seconds)
    implicit val ec:ExecutionContext = context.dispatcher

    // problem - when you need to pass argument you would use this syntax
    // this is a problem when instantiating actors inside actors, because under the hood this enclose "this", so would expose the reference of the outer actor
    // this violates the constraint of segregation of actors (share nothing)

    val ansActor = context.actorOf(Props(new AnswerActor(100)), "answerActor")

    def receive ={
      case message : SimpleMessage => {

        val future = ansActor ? message

        future onComplete (res=>
          println(res)
        )
      }
    }
  }

  class AnswerActor(seed:Int) extends Actor {

    def receive = {
      case msg : SimpleMessage => {
        sender!s"${msg.content} processed - ${Random.nextInt(seed)} "
      }
    }
  }

  // SOLUTION 1 - Creating Actors with Props

  class ABetterInnerActor() extends Actor {
    implicit val timeout = Timeout(120 seconds)
    implicit val ec:ExecutionContext = context.dispatcher

    val ansActor = context.actorOf(Props(classOf[AnswerActor], 100), "answerActor")

    def receive = {
      case message : SimpleMessage => {

        val future = ansActor ? message

        future onComplete (res=>
          println(res)
          )
      }
    }
  }

  // SOLUTION 2 - Companion Object Factory Method

  object AnswerActor {
    def props(answer:Int): Props = Props(new AnswerActor(answer))
  }

  class AnotherBetterInnerActor() extends Actor {
    implicit val timeout = Timeout(120 seconds)
    implicit val ec: ExecutionContext = context.dispatcher

    val ansActor = context.actorOf(AnswerActor.props(100), "answerActor")

    def receive = {
      case message: SimpleMessage => {

        val future = ansActor ? message

        future onComplete (res =>
          println(res)
          )
      }
    }
  }

}
