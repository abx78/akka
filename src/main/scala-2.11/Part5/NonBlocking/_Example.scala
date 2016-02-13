package Part5.NonBlocking

import akka.actor.Actor

import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Success, Failure}


class CustomerUpdate(dbDriver:DatabaseDriver) extends Actor{
  def receive ={
    case UpdateCustomer(customer) =>
      val originalSender = sender
      implicit val ec:ExecutionContext = context.system.dispatchers.lookup("my-dispatcher")

      val  future = Future {
        // some JDBC action
      }

      future.onComplete {
        case Failure(x: Throwable) => throw new CustomerUpdateException(x)
        case Success(_) => originalSender ! TransactionSuccess
      }

      import scala.concurrent.blocking

      blocking{
        // do some naughty blocking stuff
      }
  }
}


case object TransactionSuccess
case class CustomerUpdateException(val exception:Throwable) extends Throwable
case class DatabaseDriver()
case class UpdateCustomer(val customer : Customer)
case class Customer(val id:Int, val name:String)
