import akka.actor._
import akka.persistence.PersistentActor

object Intro extends App {

  val system = ActorSystem("demo")
  val actorManager = system.actorOf(Props[ManagingActor])
  actorManager!"start"

  //system.terminate()
}


class ManagingActor extends Actor with ActorLogging {

  def receive = {
    case "start" =>
      val persistentActor = context.actorOf(Props[PersistenceIntroActor], "persistentActor")

      persistentActor!setState(1)
      persistentActor!setState(2)
      persistentActor!setState(3)
      persistentActor!getState

    case state:Int => log.info(s"The state at this time is :" + state)
  }
}

class PersistenceIntroActor extends PersistentActor {

  var internalState: Int = 0

  def updateState(event:stateChangedEvent) = {

    internalState = event.state
  }

  override def receiveRecover: Receive = {
    case event:stateChangedEvent =>
      println(s"Recover state: $event" )
      updateState(event)
  }

  override def receiveCommand: Receive = {
    case setState(state) =>
      println(s"Set State Change: $state" )
      if(state >0) {
        //  Events are persisted by calling persist with an event (or a sequence of events)
        // as first argument and an event handler as second argument.
        persist(stateChangedEvent(state)) {
          // it is guaranteed that the persistent actor will not receive further commands between
          // the persist call and the execution of the associated event handler.
          event => updateState(event)
            context.system.eventStream.publish(event)
        }
      }
    case getStateValue:getState =>
      sender!internalState

  }

  override def persistenceId: String = "persistence-intro-id"
  // A persistent actor must have an identifier that doesn't change across
  // different incarnations. The identifier must be defined with the persistenceId method.
}


case class setState(state:Int)
case class getState()
case class stateChangedEvent(state:Int)


/*
The key concept behind Akka persistence is that only changes to an actor's
internal state are persisted but never its current state directly

Stateful actors are recovered by replaying stored changes to these actors
from which they can rebuild internal state. This can be either the full
history of changes or starting from a snapshot which can dramatically
reduce recovery times. Akka persistence also provides point-to-point
communication with at-least-once message delivery semantics.

PersistentActor: Is a persistent, stateful actor. It is able to persist
events to a journal and can react to them in a thread-safe manner.
It can be used to implement both command as well as event sourced actors.
When a persistent actor is started or restarted, journaled messages are
replayed to that actor, so that it can recover internal state from these
messages

The basic idea behind Event Sourcing is quite simple. A persistent actor
receives a (non-persistent) command which is first validated if it can be
applied to the current state.
If validation succeeds, events are generated from the command, representing
the effect of the command. These events are then persisted and, after
successful persistence, used to change the actor's state.

When the persistent actor needs to be recovered, only the persisted events
are replayed of which we know that they can be successfully applied.
In other words, events cannot fail when being replayed to a persistent actor,
in contrast to commands.

Event sourced actors may of course also process commands that do not change
application state, such as query commands, for example.

 */

/*

Persisting, deleting and replaying messages can either succeed or fail.

Method	                  Success	                    Failure / Rejection	              After failure handler invoked
persist / persistAsync	  persist handler invoked	    onPersistFailure	                Actor is stopped.
                                                      onPersistRejected	                No automatic actions.
recovery	                RecoveryCompleted	          onRecoveryFailure	                Actor is stopped.
deleteMessages	          DeleteMessagesSuccess	      DeleteMessagesFailure	            No automatic actions.

The most important operations (persist and recovery) have failure handlers modelled as explicit callbacks
which the user can override in the PersistentActor. The default implementations of these handlers emit a
log message (error for persist/recovery failures, and warning for others), logging the failure cause and
information about which message caused the failure.



 */

