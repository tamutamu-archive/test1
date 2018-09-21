name := "yahooFinanceData"

version := "0.1"

scalaVersion := Ver.scala

libraryDependencies ++= Dependencies.main

excludeDependencies ++= ExcludeDependencies.main

mainClass in(Compile, run) := Some("net.palm7.yahoofinance.AppMain")

lazy val slick_codegen = (project in file("slick_codegen")).settings(
  name := "slick_codegen",
  libraryDependencies ++= {
    import Library._
    Seq(slick_code, mysql_diver)
  },
  TaskKey[Unit]("slick_codegen") := (runMain in Compile).toTask(" net.palm7.yahoofinance.codegen.CodeGen").value
)
