package net.palm7.yahoofinance.fetch

import java.sql.{Date => SqlDate}
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.typesafe.config._
import net.palm7.yahoofinance.dao.Tables
import net.palm7.yahoofinance.dao.Tables.profile.api._
import net.palm7.yahoofinance.db.DBManager
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.{By, WebElement, NoSuchElementException => SeleniumNoSuchElementException}

import scala.collection.JavaConverters._

class YahooFinanceFetcher private(config: Config) extends Fetcher(config) {

  private val sdf = new SimpleDateFormat("yyyy年m月d日");

  def fetch(code: String, startDate: Date, endData: Date) = {

    val url_without_pageno = gen_url(code, startDate, endData)


    try {
      for (pageNo <- 1 to 9999) {

        println(url_without_pageno + s"&p=$pageNo")
        driver.get(url_without_pageno + s"&p=$pageNo")

        val _dailyPrices: List[WebElement] = driver.findElementByXPath("//table[contains(@class,'marB6')]/tbody").
          findElements(By.xpath("./*")).asScala.toList
        val dailyPrices = _dailyPrices.slice(1, _dailyPrices.size)

        val priceList = for {
          priceRow <- dailyPrices
        } yield {
          val ret = priceRow.getText.split(" ")

          implicit def ssss(price: String): BigDecimal = {
            BigDecimal(price.replaceAll(",", ""))
          }

          val vol = java.lang.Long.parseLong(ret(5).replaceAll(",", ""))
          Tables.PriceRow(code, sd(ret(0)), ret(1), ret(2), ret(3), ret(4), ret(6), vol)
        }

        DBManager.db.run(Tables.Price.forceInsertAll(priceList))

        isExistNext(driver, pageNo)

      }
    } catch {
      case e: SeleniumNoSuchElementException => println("!!! end !!!")
    }

  }

  private def sd(date: String): SqlDate = {
    val d = sdf.parse(date)

    val cal = Calendar.getInstance();
    cal.setTime(d);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    new SqlDate(cal.getTimeInMillis());
  }


  private def isExistNext(driver: HtmlUnitDriver, pageNo: Int) = {

    // Next page exist?
    try {
      val nextPage = pageNo + 1
      driver.findElementByXPath(s"//ul[contains(@class,'ymuiPagingBottom')]/a[text() = '$nextPage']")
    } catch {
      case e: SeleniumNoSuchElementException => throw e
    }

  }

  private def gen_url(code: String, startDate: Date, endData: Date): String = {
    val cal = Calendar.getInstance();

    cal.setTime(startDate);
    val (sy, sm, sd) = (cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))

    cal.setTime(endData);
    val (ey, em, ed) = (cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))

    s"https://info.finance.yahoo.co.jp/history/?code=$code.T&sy=$sy&sm=$sm&sd=$sd&ey=$ey&em=$em&ed=$ed&tm=d"
  }

}


object YahooFinanceFetcher {

  def apply(config: Config): YahooFinanceFetcher = {

    config.getString("http_proxy") match {
      case proxy: String => {
        val fetcher = new YahooFinanceFetcher(config) with TisProxy
        fetcher
      }
      case _ => new YahooFinanceFetcher(config)
    }
  }

}
