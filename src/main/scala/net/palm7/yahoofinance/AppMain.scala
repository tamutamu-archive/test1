package net.palm7.yahoofinance

import java.util.{Calendar, Date}

import com.typesafe.config.ConfigFactory
import net.palm7.yahoofinance.dao.Tables
import net.palm7.yahoofinance.dao.Tables.profile.api._
import net.palm7.yahoofinance.db.DBManager
import net.palm7.yahoofinance.fetch.YahooFinanceFetcher

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object AppMain {

  def main(args: Array[String]): Unit = {

    val db = DBManager("mysql_db")

    val config = ConfigFactory.load()
    val fetcher = YahooFinanceFetcher(config.getConfig("fetcher"))

    try {

      val all_code = Tables.StockCode.filter(_.marcket === "T.1")
      val code_list = Await.result(db.run(all_code.map(_.stockCode).result), Duration("10s"))


      val endDate = new Date()

      val cal = Calendar.getInstance
      cal.setTime(endDate)
      //      cal.add(Calendar.MONTH, -3)
      cal.add(Calendar.DAY_OF_MONTH, -60)
      val startDate = cal.getTime

      code_list.foreach {
        case code: String => {
          fetcher.fetch(code, startDate, endDate)
        }
        case _ => println("on no...")
      }

    } finally {
      fetcher.close
      db.close()
    }

  }

}
