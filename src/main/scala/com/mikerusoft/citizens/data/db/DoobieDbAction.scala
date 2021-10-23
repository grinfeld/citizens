package com.mikerusoft.citizens.data.db
import cats.effect.{ContextShift, IO}
import com.mikerusoft.citizens.data.db.DoobieDbAction.{Pers, SqlToString}
import doobie.implicits._
import doobie.syntax._
import com.mikerusoft.citizens.model.Types.{Invalid, Valid, Validation}
import doobie.util.fragment

import scala.concurrent.ExecutionContext

final case class DoobieDbAction[S] private[db] (transactor: doobie.Transactor[IO]) extends DBAction[S] {

  implicit def contextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  override def createConnection[T, B >: DBAction[ConnReady]](): Validation[B] = Valid(new DoobieDbAction[ConnReady](transactor))
  override def createTable[T, B >: DBAction[TableReady]](db: DBAction[ConnReady], sql: String): Validation[B] = {
    sql.toSql.update.run.transact(transactor).attemptSql.unsafeRunSync() match {
      case Right(_) => Valid(new DoobieDbAction[TableReady](transactor))
      case Left(exception) => Invalid(exception.getMessage)
    }
  }

  override def insertOfAutoIncrement[P](db: DBAction[TableReady], insertStatement: String): Validation[Int] = {
    insertStatement.toSql.update.run.transact(transactor).attemptSql.unsafeRunSync() match {
      case Right(value) => Valid(value)
      case Left(exception) => Invalid(exception.getMessage)
    }
  }

  override def selectUnique[P](db: DBAction[TableReady], selectStatement: String): Validation[P] = {
    selectStatement.toSql.query[Pers].unique.transact(transactor).attemptSql.unsafeRunSync() match {
    //selectStatement.toSql.query[P].unique.transact(transactor).attemptSql.unsafeRunSync() match {
      //case Right(value) => Valid(value)
      case Right(value) => Valid(value.asInstanceOf[P])
      case Left(exception) => Invalid(exception.getMessage)
    }
  }
}

object DoobieDbAction {
  case class Pers(id: Int, name: String)

  def apply(trans: () => doobie.Transactor[IO]): Validation[DBAction[ConnReady]] = {
    new DoobieDbAction(trans()).createConnection()
  }

  def stringToSql(value: String): fragment.Fragment = new SqlInterpolator(new StringContext(value)).sql()

  implicit class SqlToString(value: String) {
    def toSql: fragment.Fragment = new SqlInterpolator(new StringContext(value)).sql()
  }
}
