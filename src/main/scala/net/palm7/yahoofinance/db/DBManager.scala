package net.palm7.yahoofinance.db

import net.palm7.yahoofinance.dao.Tables
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.Database
import slick.lifted.Query

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import slick.jdbc.JdbcActionComponent

object DBManager {

  lazy val db = createDb

  def close {
    db.close
  }

  private def createDb = {
    Database.forConfig("mysql_db")
  }

  def apply(dbSettingName:String) = {
    db
  }

//  def executeInsertSync(action:DBIOAction[Tables.profile.ProfileAction[_,_,_],NoStream,Effect.Write]):Unit ={
//    val result = Await.result(db.run(action), Duration("10s"))
//  }

}
