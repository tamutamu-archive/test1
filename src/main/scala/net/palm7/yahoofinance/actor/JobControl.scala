package net.palm7.yahoofinance.actor

import java.util.{Calendar, Date}

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import com.typesafe.config.ConfigFactory
import net.palm7.yahoofinance.dao.Tables
import net.palm7.yahoofinance.dao.Tables.profile.api._
import net.palm7.yahoofinance.db.DBManager

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

class JobControl(attackSystem: AttackYahooSystem) extends Actor with ActorLogging {

  import JobControl._

  val config = ConfigFactory.load()

  val start = System.currentTimeMillis

  val endDate = new Date()

  val cal = Calendar.getInstance
  cal.setTime(endDate)
  //      cal.add(Calendar.MONTH, -3)
  cal.add(Calendar.DAY_OF_MONTH, -60)
  val startDate = cal.getTime

  private var stockCodeList: ListBuffer[String] = _
  private var stockCodeSize: Int = 0
  private var treatedCodeList: ListBuffer[String] = ListBuffer()


  override def receive = {
    case StartJob => startJob()
    case HttpFetcher.PleaseJobOffer => offerJob()
    case DBInsert.SuccessCode(code) => registResultWithStopDBInsert(code)
    case DBInsert.FailureCode(code) => registResultWithStopDBInsert(code)
    case Terminated(child) if child.path.name.startsWith("http_fetcher_") => {
      log.info(s"Terminated actor, $child")
      val httpFetcher = context.actorSelection("/user/job_control/http_fetcher_*").resolveOne(FiniteDuration(10, "s"))
      httpFetcher onComplete {
        case Success(x) => log.info("exist http_fetcher.")
        case Failure(e) => {
          log.info("not exist http_fetcher.")
        }
      }
    }
    case Terminated(child) if child.path.name.startsWith("db_insert") => {
      log.info(s"Terminated actor, $child")
      context.stop(self)
    }
    case _ => log.error("Don't accept message!!")
  }

  private def startJob(): Unit = {

    println("@@@@ Do output log --> ./logs/attack.log")
    log.info("@@@@@  START @@@@@")

    stockCodeList = ListBuffer.empty[String] ++= DBManager.executeSync(Tables.StockCode.filter(_.marcket === "T.1").map(_.stockCode).result)
    stockCodeSize = stockCodeList.size


    // Start DBInsert Actor.
    val dbInsert = createDBInsert()
    dbInsert ! DBInsert.CanInsert
    context.watch(dbInsert)

    // Start HttpFethcer Actor.
    val httpFetcherFunc = createHttpFetcher()
    for (i <- 1 to config.getInt("fetcher.fetcher_cnt")) {
      val httpFetcher = httpFetcherFunc(dbInsert)
      context.watch(httpFetcher)
      httpFetcher ! HttpFetcher.FetchStart
    }
  }

  protected def createHttpFetcher() = {
    var cnt = 0
    (dbInsert: ActorRef) => {
      cnt += 1
      context.actorOf(HttpFetcher.props(config, dbInsert), s"http_fetcher_$cnt")
    }
  }

  protected def createDBInsert(): ActorRef =
    context.actorOf(DBInsert.props(), "db_insert")

  private def offerJob(): Unit = {
    dequeStockCode() match {
      case Some(code) => sender() ! HttpFetcher.Fetch(code, startDate, endDate)
      case _ => context.stop(sender())
    }
  }

  private def dequeStockCode(): Option[String] = {
    stockCodeList.headOption match {
      case Some(code) => {
        val ret = Some(code)
        stockCodeList.remove(0)
        ret
      }
      case _ => None
    }
  }

  def registResultWithStopDBInsert(code: String): Unit = {
    treatedCodeList += code

    treatedCodeList.size compare stockCodeSize match {
      case 0 => context.child("db_insert") match {
        case Some(dbInsert: ActorRef) => context.stop(dbInsert)
        case None => log.info("Already dbInsert dead..")
      }
      case (-1 | 1) =>
    }

  }

  override def postStop(): Unit = {

    DBManager.close

    // attack system shutdown request.
    attackSystem.systemTerminate()

    log.info("@@@@@  END @@@@@")
    log.info((System.currentTimeMillis - start) + "ms")
  }
}


object JobControl {

  def props(attackSystem: AttackYahooSystem): Props = Props(new JobControl(attackSystem))

  case object StartJob

  case object KillAsk

}
