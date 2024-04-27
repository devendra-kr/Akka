package fault_tolerence

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}

object ActorLifecycle extends App {

  object StartChild
  class LifecycleActor extends Actor with ActorLogging {

    override def preStart(): Unit = log.info("I am starting")

    override def postStop(): Unit = log.info("I am stopped")
    override def receive: Receive = {
      case StartChild =>
        val child = context.actorOf(Props[LifecycleActor], "child")
        child ! "hello child"
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("LifecycleActor")
//  val parent = system.actorOf(Props[LifecycleActor], "parent")
//  parent ! StartChild
//  parent ! PoisonPill

  /**
   * restart
   */

  object Fail
  object FailChild
  object Check
  object CheckChild
  class Parent extends Actor {
    private val child = context.actorOf(Props[Child], "supervisedChild")
    override def receive: Receive = {
      case FailChild => child ! Fail
      case CheckChild => child ! Check
    }
  }
  class Child extends Actor with ActorLogging {

    override def preStart(): Unit = log.info("supervised Child started")

    override def postStop(): Unit = log.info("supervised Child stopped")

    override def preRestart(reason: Throwable, message: Option[Any]): Unit =
      log.info(s"supervised actor restating because of ${reason.getMessage}")

    override def postRestart(reason: Throwable): Unit =
      log.info("supervised actor restarted")

    override def receive: Receive = {
      case Fail => log.warning("child will fail now")
        throw new RuntimeException("I failed")
      case Check => log.info("alive and kicking")
    }
  }

  val supervisor = system.actorOf(Props[Parent], "supervisor")
  supervisor ! FailChild
  supervisor ! CheckChild

  // default supervision strategy
}
