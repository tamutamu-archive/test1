package net.palm7.yahoofinance.fetch

import java.sql.{Date => SqlDate}
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.typesafe.config._
import com.typesafe.scalalogging.Logger
import net.palm7.yahoofinance.dao.Tables
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.{By, WebElement, NoSuchElementException => SeleniumNoSuchElementException}

import scala.collection.JavaConverters._

class YahooFinanceFetcher private(config: Config) extends Fetcher(config) {

  val logger = Logger(this.getClass)


  override def fetch(code: String, startDate: Date, endData: Date): Either[Exception, List[Tables.PriceRow]] = {

    logger.info(s"START $code")

    val url_without_pageno = gen_url(code, startDate, endData)

    try {

      Right(getPriceListPerPage(code, url_without_pageno, 1))

    } catch {
      case e: Exception => Left(e)

    } finally {
      logger.info(s"END   $code")
    }

  }

  def createPriceRow(_code: String, _date: String, _open: String, _low: String, _high: String, _close: String, _vol: String, _adjust: String): Tables.PriceRow = {

    val sdf = new SimpleDateFormat("yyyy年M月d日")
    val d = sdf.parse(_date)

    val cal = Calendar.getInstance
    cal.setTime(d)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    val open = BigDecimal(_open.replaceAll(",", ""))
    val low = BigDecimal(_low.replaceAll(",", ""))
    val high = BigDecimal(_high.replaceAll(",", ""))
    val close = BigDecimal(_close.replaceAll(",", ""))
    val adjust = BigDecimal(_adjust.replaceAll(",", ""))

    val vol = java.lang.Long.parseLong(_vol.replaceAll(",", ""))

    Tables.PriceRow(_code, new SqlDate(cal.getTimeInMillis()), open, low, high, close, adjust, vol)
  }


  private def getPriceListPerPage(code: String, baseURL: String, pageNo: Int): List[Tables.PriceRow] = {

    logger.info(baseURL + s"&p=$pageNo")
    driver.get(baseURL + s"&p=$pageNo")

    val _dailyPrices: List[WebElement] = driver.findElementByXPath("//table[contains(@class,'marB6')]/tbody").
      findElements(By.xpath("./*")).asScala.toList
    val dailyPrices =
      _dailyPrices.slice(1, _dailyPrices.size).
        map(pr =>
          pr.getText.split(" ")
        )

    val priceList: List[Tables.PriceRow] = for {
      pr <- dailyPrices if pr.length == 7
    } yield {
      createPriceRow(code, pr(0), pr(1), pr(2), pr(3), pr(4), pr(5), pr(6))
    }

    isExistNext(driver, pageNo) match {
      case true => priceList ::: getPriceListPerPage(code, baseURL, {
        pageNo + 1
      })
      case false => priceList
    }

  }

  private def isExistNext(driver: HtmlUnitDriver, pageNo: Int): Boolean = {

    // Next page exist?
    try {
      val nextPage = pageNo + 1
      driver.findElementByXPath(s"//ul[contains(@class,'ymuiPagingBottom')]/a[text() = '$nextPage']")
      true

    } catch {
      case e: SeleniumNoSuchElementException => false
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

    config.getString("fetcher.http_proxy") match {
      case proxy: String => {
        val fetcher = new YahooFinanceFetcher(config) with TisProxy
        fetcher
      }
      case _ => new YahooFinanceFetcher(config)
    }
  }

}
