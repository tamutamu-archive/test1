package net.palm7.yahoofinance.actor

import java.util.Date

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.typesafe.config.Config
import net.palm7.yahoofinance.fetch.{Fetcher, YahooFinanceFetcher}

class HttpFetcher(config: Config, dbInsert: ActorRef) extends Actor with ActorLogging {

  import HttpFetcher._

  lazy val fetcher: Fetcher = YahooFinanceFetcher(config)

  override def receive: Receive = {

    case FetchStart => {
      sender() ! PleaseJobOffer
    }
    case Fetch(code, startDate, endDate) => {
      fetch(code, startDate, endDate)
      sender() ! PleaseJobOffer
    }
    case _ => log.error("Don't accept message!!")
  }

  def fetch(code: String, startDate: Date, endDate: Date) = {

    fetcher.fetch(code, startDate, endDate) match {
      case Right(priceList) => {
        dbInsert ! DBInsert.InsertPriceList(code, priceList)
      }
      case Left(e) => log.error(e.getMessage)
    }

  }

}

object HttpFetcher {

  def props(config: Config, dbInsert: ActorRef): Props = Props(new HttpFetcher(config, dbInsert))

  case class Fetch(val code: String, val startDate: Date, val endDate: Date)

  case object FetchStart

  case object PleaseJobOffer

}
