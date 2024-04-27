package infrastructure

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props, Timers}

import scala.concurrent.duration._

object TimerSchedulers extends App {

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("SchedulersTimer")
  val simpleActor = system.actorOf(Props[SimpleActor])

  //system.log.info("Scheduling reminder for simpleActor")


 /* system.scheduler.scheduleOnce(1.second){
    simpleActor ! "reminder"
  } (system.dispatcher)*/


/*  implicit val executionContext =  system.dispatcher
  system.scheduler.scheduleOnce(1.second){
    simpleActor ! "reminder"
  }*/

  import system.dispatcher

//  system.scheduler.scheduleOnce(1.second){
//    simpleActor ! "reminder"
//  }
//
//  val routine: Cancellable = system.scheduler.schedule(1.second, 2.second) {
//    simpleActor ! "heartbeat"
//  }
//
//  system.scheduler.scheduleOnce(5.second) {
//    routine.cancel()
//  }

  /**
   * Exercise: Implement a self-closing actor
   */

  class SelfClosingActor extends Actor with ActorLogging {

    var schedule = createTimeoutWindow()
    def createTimeoutWindow(): Cancellable = {
      context.system.scheduler.scheduleOnce(1.second) {
        self ! "timeout"
      }
    }
    override def receive: Receive = {
      case "timeout" =>
        log.info("Stopping myself!")
        context.stop(self)
      case message =>
        log.info(s"Receive $message, staying alive")
        schedule.cancel()
        schedule = createTimeoutWindow()
    }
  }

//  val selfClosingActor = system.actorOf(Props[SelfClosingActor], "selfClosingActor")
//  system.scheduler.scheduleOnce(250.millis) {
//    selfClosingActor ! "ping"
//  }
//
//  system.scheduler.scheduleOnce(2.second) {
//    system.log.info("sending pong to the self-closing actor")
//    selfClosingActor ! "pong"
//  }

  /**
   * Timer
   */

  case object TimeKey
  case object Start
  case object Remider
  case object Stop

  class TimerBasedHeartBeatActor extends Actor with ActorLogging with Timers {
    timers.startSingleTimer(TimeKey, Start, 500.millis)

    override def receive: Receive = {
      case Start =>
        log.info("Bootstrapping")
        timers.startPeriodicTimer(TimeKey, Remider, 1.second)
      case Remider =>
        log.info("I am alive")
      case Stop =>
        log.info("Stopping")
        timers.cancel(TimeKey)
        context.stop(self)
    }
  }

  val timerBasedHeartBeatActor = system.actorOf(Props[TimerBasedHeartBeatActor], "timerBasedHeartBeatActor")
  system.scheduler.scheduleOnce(5.second) {
    timerBasedHeartBeatActor ! Stop
  }

}
