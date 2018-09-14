package net.palm7.yahoofinance.fetch

import org.openqa.selenium.{Proxy => SeleniumProxy}

trait TisProxy {

  this: Fetcher =>

  val proxy: SeleniumProxy = new SeleniumProxy()
  proxy.setHttpProxy(config.getString("http_proxy"))
  driver.setProxySettings(proxy)

}
