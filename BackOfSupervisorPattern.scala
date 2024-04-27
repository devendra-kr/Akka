package fault_tolerence

import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props}
import akka.pattern.{BackoffOpts, BackoffSupervisor}

import scala.io.Source
import java.io.File
import scala.concurrent.duration._
import akka.actor.SupervisorStrategy.Stop

object BackOfSupervisorPattern extends App {

  case object ReadFile
  class FileBasedPersistentActor extends Actor with ActorLogging {
    var dataSource: Source = null

    override def preStart(): Unit = {
      log.info("Persistent actor starting")
    }

    override def postStop(): Unit = {
      log.info("Persistent actor has stopped")
    }

    override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
      log.warning("Persistent actor restarting")
    }

    override def receive: Receive = {
      case ReadFile =>
        if(dataSource == null) {
          dataSource = Source.fromFile(new File("src/main/resources/testfiles/important_date.txt"))
          log.info("I've just read some Important data: " + dataSource.getLines().toList)
        }
    }
  }

  val system = ActorSystem("BackOfSupervisorPattern")
//  val simpleActor = system.actorOf(Props[FileBasedPersistentActor], "simpleActor")
//  simpleActor ! ReadFile

  val simpleSupervisorProps = BackoffSupervisor.props(
    BackoffOpts.onFailure(
    Props[FileBasedPersistentActor],
      "simpleBackoffActor",
      3.seconds,
      30.seconds,
      0.2
    )
  )

//  val simpleBackoffSupervisor = system.actorOf(simpleSupervisorProps, "simpleSupervisor")
//
//  simpleBackoffSupervisor ! ReadFile

  /**
   * simpleSupervisor
   *  -- child called simpleBackoffActor (props of type FileBasedPersistentActor)
   *  -- supervisor strategy is the default on (restring on everything)
   *    -- first attempt after 3 seconds
   *    -- next attempt is 2x the previous attempt
   *
   */

  val stopSupervisorProps = BackoffSupervisor.props(
    BackoffOpts.onStop(
      Props[FileBasedPersistentActor],
      "stopBackoffActor",
      3.seconds,
      30.seconds,
      0.2
    )withSupervisorStrategy(
      OneForOneStrategy() {
        case _ => Stop
      }
    )
  )

//  val stopSupervisor = system.actorOf(stopSupervisorProps, "stopSupervisor")
//  stopSupervisor ! ReadFile

  class EagerFBPActor extends FileBasedPersistentActor {
    override def preStart(): Unit = {
      log.info("Eager actor staring")
      dataSource = Source.fromFile(new File("src/main/resources/testfiles/important_date.txt"))
    }
  }

  //val eagerFBPActor = system.actorOf(Props[EagerFBPActor])
  //ActorInitializationException ==> Stop

  val repeatedSupervisorProps = BackoffSupervisor.props(
    BackoffOpts.onStop(
      Props[EagerFBPActor],
      "eagerActor",
      1.seconds,
      10.seconds,
      0.1
    )
  )

  val repeatedSupervisor = system.actorOf(repeatedSupervisorProps, "eagerSupervisor")

  /**
   * eagerSupervisor
   *  -- child eagerActor
   *      -- will die on start with ActorInitializationException
   *      -- trigger the supervisor strategy in eagerSupervisor => STOP eagerActor
   *  -- backoff will kick in after 1 seconds, 2s, 4, 8, 16
   */



}
