package net.palm7.yahoofinance.actor

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.scalalogging.Logger

class AttackYahooSystem(system: ActorSystem) {

  val logger = Logger(this.getClass)


  import net.palm7.yahoofinance.actor.JobControl._

  val jobControl = createJobControl(this)

  def run() = {
    jobControl ! StartJob
  }

  def systemTerminate() = {
    system.terminate()
  }

  private def createJobControl(attackSystem: AttackYahooSystem): ActorRef = {
    system.actorOf(JobControl.props(attackSystem), "job_control")
  }

}

object AttackYahooSystem {

  def apply() = {
    val system = ActorSystem("attack-yahoo")
    new AttackYahooSystem(system)
  }

}
