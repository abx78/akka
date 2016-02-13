import Stuff.SimpleMessage
import akka.actor._

object StreamingWithLogger extends App {

  val system = ActorSystem("streamingSystem")
  val actorStream = system.actorOf(Props[LoggedActor], "loggedActor")

  val dlListener = system.actorOf(Props[DeadLetterListenerActor], "dlListener")

  // subscribe -> EventBus, takes subscriber + classifier
  system.eventStream.subscribe(dlListener, classOf[DeadLetter])

  actorStream!new SimpleMessage("a message!")

  class LoggedActor extends Actor with ActorLogging {
    // By default, the Actor that subscribes to these messages is the DefaultLogger which
    // simply prints the message to the standard output

    def receive = {
      case message:SimpleMessage=>{
        log.info(message.content)
        sender!"hi"
      }
    }
  }

  class DeadLetterListenerActor extends Actor  {
    def receive = {
      case d: DeadLetter => println(d)
    }
  }
}


object EventsListener extends App {

  val system = ActorSystem("streamingSystem")

  val eventListener = system.actorOf(Props[ListenerActor], "eventStreamActor")
  system.eventStream.subscribe(eventListener, classOf[SimpleMessage])

  system.eventStream.publish(SimpleMessage("a streamed message"))

  class ListenerActor extends Actor  {

    def receive = {
      case message:SimpleMessage=>{
        println(message.content)
      }
    }
  }
}


object ExampleFromDocAkkaIo extends App {

  val system = ActorSystem("musicStreamingSystem")

  abstract class AllKindsOfMusic { def artist: String }
  case class Jazz(artist: String) extends AllKindsOfMusic
  case class Electronic(artist: String) extends AllKindsOfMusic


  class Listener extends Actor {
    def receive = {
      case m: Jazz => println(s"${self.path.name} is listening to: ${m.artist}")
      case m: Electronic => println(s"${self.path.name} is listening to: ${m.artist}")
    }
  }

  val jazzListener = system.actorOf(Props(classOf[Listener]), "jazzlistner")
  val musicListener = system.actorOf(Props(classOf[Listener]), "muscilistner")
  system.eventStream.subscribe(jazzListener, classOf[Jazz])
  system.eventStream.subscribe(musicListener, classOf[AllKindsOfMusic])

  // only musicListener gets this message, since it listens to *all* kinds of music:
  system.eventStream.publish(Electronic("Parov Stelar"))

  // jazzListener and musicListener will be notified about Jazz:
  system.eventStream.publish(Jazz("Sonny Rollins"))
}

