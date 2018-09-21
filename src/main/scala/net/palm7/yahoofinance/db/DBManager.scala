package net.palm7.yahoofinance.db

import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object DBManager {

  lazy val db = createDb

  def close {
    db.close
  }

  def apply(dbSettingName: String) = {
    db
  }

  def executeSync[R](action: DBIOAction[R, NoStream, Nothing], duration: Duration = Duration("10s")) = {
    Await.result(db.run(action), duration)
  }

  def executeAsync[R](action: DBIOAction[R, NoStream, Nothing], duration: Duration = Duration("10s")) = {
    db.run(action)
  }

  private def createDb = {
    Database.forConfig("mysql_db")
  }

}
