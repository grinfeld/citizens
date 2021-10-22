package com.mikerusoft.citizens.data.db
import cats.effect.{ContextShift, IO}
import doobie.implicits._
import doobie.syntax._
import com.mikerusoft.citizens.model.Types.{Invalid, Valid, Validation}
import doobie.util.fragment

import scala.concurrent.ExecutionContext

final case class DoobieDbAction[S] private[db] (transactor: doobie.Transactor[IO]) extends DBAction[S] {

  case class Pers(id: Int, name: String)

  implicit def contextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  override def createConnection[T, B >: DBAction[ConnReady]](): Validation[B] = Valid(new DoobieDbAction[ConnReady](transactor))
  override def createTableIfNotExists[T, B >: DBAction[TableReady]](db: DBAction[ConnReady]): Validation[B] = {
    sql"CREATE TABLE person (id INT auto_increment, name VARCHAR(256))".update.run.transact(transactor).attemptSql.unsafeRunSync() match {
      case Right(_) => Valid(new DoobieDbAction[TableReady](transactor))
      case Left(exception) => Invalid(exception.getMessage)
    }
  }

  override def insertOfAutoIncrement[P](db: DBAction[TableReady], insertStatement: String): Validation[Int] = {
    new SqlInterpolator(new StringContext(insertStatement)).sql().update.run.transact(transactor).attemptSql.unsafeRunSync() match {
      case Right(value) => Valid(value)
      case Left(exception) => Invalid(exception.getMessage)
    }
  }
}

object DoobieDbAction {
  def apply(trans: () => doobie.Transactor[IO]): Validation[DBAction[ConnReady]] = {
    new DoobieDbAction(trans()).createConnection()
  }

  def stringToSql(value: String): fragment.Fragment = new SqlInterpolator(new StringContext(value)).sql()

  implicit class SqlToString(value: String) {
    def toSql: fragment.Fragment = new SqlInterpolator(new StringContext(value)).sql()
  }
}
