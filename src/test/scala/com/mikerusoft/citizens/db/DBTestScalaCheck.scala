package com.mikerusoft.citizens.db

import cats.data.Validated
import cats.effect.IO
import cats.effect._
import com.mikerusoft.citizens.data.db.DoobieDbAction.SqlToString
import com.mikerusoft.citizens.data.db.{DBAction, DoobieDbAction, TableReady}
import com.mikerusoft.citizens.model.Types.{ErrorMsg, Invalid, Valid}
import doobie._
import doobie.implicits._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext

class DBTestScalaCheck extends AnyFlatSpec with Matchers with doobie.scalatest.IOChecker {

  implicit def contextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  val transactor: doobie.Transactor[IO] = {
    Transactor.fromDriverManager[IO](
      "org.h2.Driver",
      "jdbc:h2:mem:MyDatabase;DB_CLOSE_DELAY=-1", // connect URL (driver-specific)
      "sa", // user
      ""
    )
  }

  case class Pers(id: Int, name: String)

  "stam test" should "with doobie and H2" in {

    val db: Validated[ErrorMsg, DBAction[TableReady]] = DoobieDbAction(() => transactor).andThen(db => db.createTableIfNotExists(db))

    db match {
      case Valid(db) =>
        db.insertOfAutoIncrement(db, "insert into person (id, name) values (null, 'Misha')") match {
          case Valid(id) =>
            s"select id,name from person where id=$id".toSql.query[Pers].unique.transact(transactor).attemptSql.unsafeRunSync() match {
              case Right(p) => println(p)
              case Left(exception) => println(exception.getMessage)
          }
          case Invalid(e) => println(e)
        }
      case Invalid(error) => println(error)
    }


/*    (sql"CREATE TABLE person (id INT auto_increment, name VARCHAR(256))".update.run.transact(transactor).attemptSql.unsafeRunSync() match {
      case Right(_) => sql"insert into person (id, name) values (null, 'Misha')".update.run.transact(transactor).attemptSql.unsafeRunSync() match {
        case Right(_) => Right(sql"select id,name from person where id = 1".query[Pers].unique.transact(transactor).unsafeRunSync())
        case Left(value) => Left(value)
      }
      case Left(value) => Left(value)
    }) match {
      case Right(value) => println(value)
      case Left(ex) => ex.printStackTrace()
    }*/

  }
}