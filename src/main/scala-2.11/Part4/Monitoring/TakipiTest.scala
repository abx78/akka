package Part4.Monitoring

import akka.actor._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._



object TakipiTest extends App {

  var sys = ActorSystem("TakipiTestSystem")

  implicit val ec:ExecutionContext = sys.dispatcher

  val someChild = sys.actorOf(Props[SomeChild], "SomeChild")
  val lazyChild = sys.actorOf(Props[LazyChild], "LazyChild")

  val supervisor = sys.actorOf(Props(new Supervisor(someChild, lazyChild)), "Supervisor")

  println(supervisor.path)

  sys.scheduler.schedule(1.second, 1.second){
    supervisor!StartJob
  }
}

class Supervisor(someChild: ActorRef, lazyChild: ActorRef) extends Actor {
  implicit val ec:ExecutionContext = context.dispatcher

  def receive = {
    case JobDone =>
      println("a job has been completed")

    case StartJob => {

      val rnd = math.random
      println(s"job started $rnd")

      if(rnd < 0.5)
        lazyChild!StartJob
      else
        someChild!StartJob
    }
  }
}

class LazyChild extends Actor {
  implicit val ec:ExecutionContext = context.dispatcher
  def receive: Receive = {
    case StartJob =>
      println("lazy dude starting")
      val originalSender = sender()
      context.system.scheduler.scheduleOnce(1.seconds) {
        Thread.sleep(2000)
        originalSender!JobDone
      }
  }
}

class SomeChild extends Actor {
  implicit val ec:ExecutionContext = context.dispatcher
  def receive: Receive = {
    case StartJob =>
      println("some dude starting")
      val originalSender = sender()
      context.system.scheduler.scheduleOnce(1.seconds) {
        Thread.sleep(1000)
        originalSender!JobDone
      }
  }
}

case class StartJob()
case class JobDone()