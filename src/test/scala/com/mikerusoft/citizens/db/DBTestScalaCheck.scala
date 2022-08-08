package com.mikerusoft.citizens.db

import cats.effect.{IO, _}
import com.mikerusoft.citizens.data.db.DoobieDbAction
import com.mikerusoft.citizens.data.db.DoobieDbAction._
import com.mikerusoft.citizens.model.Types.{Invalid, Valid}
import doobie._
import doobie.implicits._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext

class DBTestScalaCheck extends AnyFlatSpec with Matchers with doobie.scalatest.IOChecker {

  implicit def contextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  implicit val transactor: doobie.Transactor[IO] = {
    Transactor.fromDriverManager[IO](
      "com.mysql.cj.jdbc.Driver",
      "jdbc:mysql://localhost:3306/misha", // connect URL (driver-specific)
      "misha", // user
      "misha"
    )
  }

  "stam test" should "with doobie and H2" in {


    val db = DoobieDbAction.create.andThen(db => db.createTableWithFragment(db, sql"CREATE TABLE if not exists persons (id INT auto_increment, name VARCHAR(256), PRIMARY KEY (id))"))

    db match {
      case Valid(db) =>
        db.insertOfAutoIncrementWithFragment(db, sql"insert into persons (id, name) values (null, 'Misha')") match {
          case Valid(id) =>
            implicit val r = Read[Pers]
            val v = db.selectUnique(db, "select id,name from persons where id = ${id}")
            v match {
              case Valid(a) =>
                println(a)
                db.selectListWithFragment(db, sql"select id,name from persons where name='Misha'") match {
                  case Valid(list) => list.foreach(p => println(s"$p"))
                  case Invalid(e) => println(e)
                }
              case Invalid(e) => println(e)
            }
          case Invalid(e) => println(e)
        }
      case Invalid(error) => println(error)
    }


/*    (sql"CREATE TABLE if not exists person (id INT auto_increment, name VARCHAR(256), PRIMARY KEY (id))".update.run.transact(transactor).attemptSql.unsafeRunSync() match {
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