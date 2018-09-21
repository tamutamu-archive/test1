import sbt._

object Ver {
  val logback   = "1.2.3"
  val scala     = "2.12.6"
  val scalaloggig     = "3.9.0"
  val slick     = "3.2.3"
  val mysql = "6.0.6"
  val config = "1.3.1"
  val selenium = "3.14.0"
  val htmlunit = "2.32.1"
  val jcl_over = "1.7.25"
  val jul_to = "1.7.25"

  val akkaVersion = "2.5.4"
}

object Library {

  val akka_actor = "com.typesafe.akka" %% "akka-actor" % Ver.akkaVersion
  val akka_slf4j = "com.typesafe.akka" %% "akka-slf4j" % Ver.akkaVersion

  val logback_classic = "ch.qos.logback" % "logback-classic" % Ver.logback
  val jcl_over = "org.slf4j" % "jcl-over-slf4j" % Ver.jcl_over
  val jul_to = "org.slf4j" % "jul-to-slf4j" % Ver.jul_to
  val scala_logging = "com.typesafe.scala-logging" %% "scala-logging" % Ver.scalaloggig

  val mysql_diver = "mysql" % "mysql-connector-java" % Ver.mysql
  val slick = "com.typesafe.slick" %% "slick" % Ver.slick
  val slick_hikaricp = "com.typesafe.slick" % "slick-hikaricp_2.12" % Ver.slick
  val slick_code = "com.typesafe.slick" %% "slick-codegen" % Ver.slick

  val config = "com.typesafe" % "config" % Ver.config

  val selenium_java = "org.seleniumhq.selenium" % "selenium-java" % Ver.selenium
  val htmlunit = "org.seleniumhq.selenium" % "htmlunit-driver" % Ver.htmlunit

}

object Dependencies {
  import Library._

  def main = List(
    logback_classic,
    jcl_over,
    jul_to,
    scala_logging,
    akka_actor,
    akka_slf4j,
    mysql_diver,
    slick,
    slick_hikaricp,
    config,
    selenium_java,
    htmlunit
  )
}

object ExcludeDependencies{

  def main = List(
    "commons-logging" % "commons-logging",
  )

}
