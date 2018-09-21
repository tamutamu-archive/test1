package net.palm7.yahoofinance.fetch

import java.net.{MalformedURLException, URL}
import java.util.Date

import com.gargoylesoftware.htmlunit._
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener
import com.typesafe.config.Config
import net.palm7.yahoofinance.dao.Tables
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}

abstract class Fetcher(val config: Config) {

  val cap = DesiredCapabilities.htmlUnit
  cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
  cap.setVersion(BrowserVersion.FIREFOX_52.toString)

  val driver = new HtmlUnitDriver(cap) {
    override def getWebClient(): WebClient = {
      var webc = super.getWebClient()
      var opt = webc.getOptions

      opt.setJavaScriptEnabled(true);
      opt.setRedirectEnabled(true);
      opt.setThrowExceptionOnScriptError(false);
      opt.setCssEnabled(false);
      opt.setUseInsecureSSL(true);
      opt.setThrowExceptionOnFailingStatusCode(false);
      webc.getCookieManager().setCookiesEnabled(true);

      /** * TODO Use ajaxController. Currentry disabled sincnce memory leak??
        *webc.setAjaxController(new NicelyResynchronizingAjaxController());
        * **/

      webc.setIncorrectnessListener(new IncorrectnessListener() {
        override def notify(arg0: String, arg1: Object): Unit = {}
      })
      webc.setCssErrorHandler(new SilentCssErrorHandler())

      webc.setJavaScriptErrorListener(new JavaScriptErrorListener() {
        override def timeoutError(arg0: HtmlPage, arg1: Long, arg2: Long): Unit = {}
        override def scriptException(page: HtmlPage, scriptException: ScriptException): Unit = {}
        override def malformedScriptURL(arg0: HtmlPage, arg1: String, arg2: MalformedURLException): Unit = {}
        override def loadScriptError(page:HtmlPage, scriptUrl:URL, exception:Exception): Unit = {}
      })

      webc
    }
  }

  def fetch(code: String, startDate: Date, endData: Date): Either[Exception, List[Tables.PriceRow]]

  def close: Unit = {
    driver.close()
  }

}
