package net.palm7.yahoofinance

import java.util.{Calendar, Date}

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import net.palm7.yahoofinance.dao.Tables
import net.palm7.yahoofinance.dao.Tables.profile.api._
import net.palm7.yahoofinance.db.DBManager
import net.palm7.yahoofinance.fetch.YahooFinanceFetcher

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.typesafe.scalalogging.Logger
import net.palm7.yahoofinance.actor.AttackYahooSystem
import org.slf4j.bridge.SLF4JBridgeHandler


object AppMain {

  SLF4JBridgeHandler.removeHandlersForRootLogger()
  SLF4JBridgeHandler.install()

  def main(args: Array[String]): Unit = {

    try {

      AttackYahooSystem().run()

    } finally {

    }

  }

}
