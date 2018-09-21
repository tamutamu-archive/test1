package net.palm7.yahoofinance.actor


import akka.actor.{Actor, ActorLogging, Props}
import net.palm7.yahoofinance.dao.Tables
import net.palm7.yahoofinance.dao.Tables.profile.api._
import net.palm7.yahoofinance.db.DBManager

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.duration.Duration
import scala.util.Failure

class DBInsert extends Actor with ActorLogging {

  import DBInsert._

  override def receive: Receive = {
    case CanInsert => context.become(receiveCanInsert)
    case InsertPriceList(code, priceList) => log.error("Don't accept insert message!!")
    case _ => log.error("Don't accept message!!")
  }


  def receiveCanInsert: Receive = {
    case InsertPriceList(code, priceList) => {
      val dbInsertFut = Future {
        DBManager.executeAsync(Tables.Price.forceInsertAll(priceList), Duration("10s"))
      }

      dbInsertFut onComplete {
        case Success(value) => context.parent ! SuccessCode(code)
        case Failure(exception) =>
      }
    }
    case NotCanInsert => context.unbecome()
    case _ => log.error("Don't accept message!!")
  }
}

object DBInsert {

  def props() = Props(new DBInsert())

  case class InsertPriceList(code: String, priceList: List[Tables.PriceRow])

  case class SuccessCode(code: String)

  case class FailureCode(code: String)

  case object CanInsert

  case object NotCanInsert

}
