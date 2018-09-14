package net.palm7.yahoofinance.dao
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Price.schema ++ StockCode.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Price
   *  @param stockCode Database column stock_code SqlType(VARCHAR), Length(6,true)
   *  @param stockDate Database column stock_date SqlType(DATE)
   *  @param open Database column open SqlType(DECIMAL)
   *  @param lower Database column lower SqlType(DECIMAL)
   *  @param high Database column high SqlType(DECIMAL)
   *  @param close Database column close SqlType(DECIMAL)
   *  @param adjust Database column adjust SqlType(DECIMAL)
   *  @param vol Database column vol SqlType(BIGINT) */
  case class PriceRow(stockCode: String, stockDate: java.sql.Date, open: scala.math.BigDecimal, lower: scala.math.BigDecimal, high: scala.math.BigDecimal, close: scala.math.BigDecimal, adjust: scala.math.BigDecimal, vol: Long)
  /** GetResult implicit for fetching PriceRow objects using plain SQL queries */
  implicit def GetResultPriceRow(implicit e0: GR[String], e1: GR[java.sql.Date], e2: GR[scala.math.BigDecimal], e3: GR[Long]): GR[PriceRow] = GR{
    prs => import prs._
    PriceRow.tupled((<<[String], <<[java.sql.Date], <<[scala.math.BigDecimal], <<[scala.math.BigDecimal], <<[scala.math.BigDecimal], <<[scala.math.BigDecimal], <<[scala.math.BigDecimal], <<[Long]))
  }
  /** Table description of table price. Objects of this class serve as prototypes for rows in queries. */
  class Price(_tableTag: Tag) extends profile.api.Table[PriceRow](_tableTag, Some("stock_db"), "price") {
    def * = (stockCode, stockDate, open, lower, high, close, adjust, vol) <> (PriceRow.tupled, PriceRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(stockCode), Rep.Some(stockDate), Rep.Some(open), Rep.Some(lower), Rep.Some(high), Rep.Some(close), Rep.Some(adjust), Rep.Some(vol)).shaped.<>({r=>import r._; _1.map(_=> PriceRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column stock_code SqlType(VARCHAR), Length(6,true) */
    val stockCode: Rep[String] = column[String]("stock_code", O.Length(6,varying=true))
    /** Database column stock_date SqlType(DATE) */
    val stockDate: Rep[java.sql.Date] = column[java.sql.Date]("stock_date")
    /** Database column open SqlType(DECIMAL) */
    val open: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("open")
    /** Database column lower SqlType(DECIMAL) */
    val lower: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("lower")
    /** Database column high SqlType(DECIMAL) */
    val high: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("high")
    /** Database column close SqlType(DECIMAL) */
    val close: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("close")
    /** Database column adjust SqlType(DECIMAL) */
    val adjust: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("adjust")
    /** Database column vol SqlType(BIGINT) */
    val vol: Rep[Long] = column[Long]("vol")

    /** Primary key of Price (database name price_PK) */
    val pk = primaryKey("price_PK", (stockCode, stockDate))

    /** Foreign key referencing StockCode (database name price_ibfk_1) */
    lazy val stockCodeFk = foreignKey("price_ibfk_1", stockCode, StockCode)(r => r.stockCode, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Price */
  lazy val Price = new TableQuery(tag => new Price(tag))

  /** Entity class storing rows of table StockCode
   *  @param stockCode Database column stock_code SqlType(VARCHAR), PrimaryKey, Length(6,true)
   *  @param name Database column name SqlType(VARCHAR), Length(50,true)
   *  @param marcket Database column marcket SqlType(VARCHAR), Length(4,true), Default(None) */
  case class StockCodeRow(stockCode: String, name: String, marcket: Option[String] = None)
  /** GetResult implicit for fetching StockCodeRow objects using plain SQL queries */
  implicit def GetResultStockCodeRow(implicit e0: GR[String], e1: GR[Option[String]]): GR[StockCodeRow] = GR{
    prs => import prs._
    StockCodeRow.tupled((<<[String], <<[String], <<?[String]))
  }
  /** Table description of table stock_code. Objects of this class serve as prototypes for rows in queries. */
  class StockCode(_tableTag: Tag) extends profile.api.Table[StockCodeRow](_tableTag, Some("stock_db"), "stock_code") {
    def * = (stockCode, name, marcket) <> (StockCodeRow.tupled, StockCodeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(stockCode), Rep.Some(name), marcket).shaped.<>({r=>import r._; _1.map(_=> StockCodeRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column stock_code SqlType(VARCHAR), PrimaryKey, Length(6,true) */
    val stockCode: Rep[String] = column[String]("stock_code", O.PrimaryKey, O.Length(6,varying=true))
    /** Database column name SqlType(VARCHAR), Length(50,true) */
    val name: Rep[String] = column[String]("name", O.Length(50,varying=true))
    /** Database column marcket SqlType(VARCHAR), Length(4,true), Default(None) */
    val marcket: Rep[Option[String]] = column[Option[String]]("marcket", O.Length(4,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table StockCode */
  lazy val StockCode = new TableQuery(tag => new StockCode(tag))
}
