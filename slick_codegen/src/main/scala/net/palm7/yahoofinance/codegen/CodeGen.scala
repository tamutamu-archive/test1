package net.palm7.yahoofinance.codegen

import slick.codegen.SourceCodeGenerator
import com.typesafe.config.{Config, ConfigFactory}

object CodeGen extends App {

  implicit val config = ConfigFactory.load()

  val profile = dcg("mysql_db.profile").replaceAll("\\$$", "")
  val jdbcDriver = dcg("mysql_db.driver")
  val url = dcg("mysql_db.url")
  val user = dcg("mysql_db.user")
  val password = dcg("mysql_db.password")

  val outputDir = dcg("slick_codegen.outputDir")
  val pkg = dcg("slick_codegen.pkg")


  SourceCodeGenerator.main(
    Array(profile, jdbcDriver, url, outputDir, pkg, user, password))


  def dcg(name:String)(implicit config:Config):String = {
    config.getString(name)
  }

}
