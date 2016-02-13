package Part1.Intro

import akka.actor.{Identify, Actor, Props, ActorSystem}

import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Success, Failure}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object _0Intro_Paths extends App {

  val system = ActorSystem("demo")

  val actorParentA = system.actorOf(Props[ParentActor], "ParentA")
  val actorParentB = system.actorOf(Props[ParentActor], "ParentB")

  val res = system.child("cccccc")
  println(s"parent: ${res.parent.name} child: ${res.name}")

  actorParentA!"test"
  actorParentB!"test"

  class ParentActor extends Actor {

    context.actorOf(Props[ChildActor], "Child1")
    context.actorOf(Props[ChildActor], "Child2")
    context.actorOf(Props[ChildActor], "Child3")

    def receive = {
      case msg: String =>
        println(s"I am: ${self.path}; My parent is: ${context.parent.path}; Here my children: ${context.children.map(_.path).map(_.toString)}")
    }
  }

  class ChildActor extends Actor {

    def receive = {
      case msg: String =>
        println(s"I am: ${self.path}; My parent is: ${context.parent.path}; Here my children: ${context.children.map(_.path).map(_.toString)}")
    }
  }

  system.scheduler.scheduleOnce(100.milli){
    val selection = system.actorSelection("/user/ParentA")
    selection.resolveOne(100.milli).onComplete{
      case Success(actor) =>
        println("*Selection test*")
        actor!"test"
      case Failure(ex) =>
        println(s"Nothing found")
    }
  }

}

