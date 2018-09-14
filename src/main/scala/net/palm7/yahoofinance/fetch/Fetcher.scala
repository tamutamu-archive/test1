package net.palm7.yahoofinance.fetch

import com.gargoylesoftware.htmlunit.{IncorrectnessListener, NicelyResynchronizingAjaxController, SilentCssErrorHandler, WebClient}
import com.typesafe.config.Config
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}

abstract class Fetcher(val config:Config) {

  val cap = DesiredCapabilities.htmlUnit
  cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
  //  cap.setVersion(FIREFOX_52.toString)

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
      webc.setAjaxController(new NicelyResynchronizingAjaxController());

      webc.setIncorrectnessListener(new IncorrectnessListener() {
        override def notify(arg0: String, arg1: Object): Unit = {}
      })
      webc.setCssErrorHandler(new SilentCssErrorHandler())

      webc
    }
  }

  def close:Unit = {
    driver.close()
  }

}
