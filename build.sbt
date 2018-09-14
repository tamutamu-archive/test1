name := "yahooFinanceData"

version := "0.1"

scalaVersion := "2.12.6"

val slickVersion = "3.2.3"

libraryDependencies ++= {
  Seq(
    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.typesafe.slick" % "slick-hikaricp_2.12" % slickVersion,
    "com.typesafe" % "config" % "1.3.1",
    "mysql" % "mysql-connector-java" % "6.0.6",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "org.seleniumhq.selenium" % "selenium-java" % "3.6.0",
    "org.seleniumhq.selenium" % "htmlunit-driver" % "2.27"
  )
}

mainClass in(Compile, run) := Some("net.palm7.yahoofinance.AppMain")

lazy val slick_codegen = (project in file("slick_codegen")).settings(
  name := "slick_codegen",
  libraryDependencies ++=
    Seq("com.typesafe.slick" %% "slick-codegen" % slickVersion, "mysql" % "mysql-connector-java" % "6.0.6"),
  TaskKey[Unit]("slick_codegen") := (runMain in Compile).toTask(" net.palm7.yahoofinance.codegen.CodeGen").value
)
