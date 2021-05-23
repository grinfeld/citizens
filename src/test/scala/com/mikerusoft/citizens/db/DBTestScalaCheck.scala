package com.mikerusoft.citizens.db

import cats.effect.IO
import cats.effect._
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

    sql"CREATE  TABLE person (id   INT, name VARCHAR(256))".update.run.transact(transactor).unsafeRunSync()

    sql"insert into person (id, name) values (1, 'Misha')".update.run.transact(transactor).unsafeRunSync()

    val select = sql"select id,name from person"
      .query[Pers].unique.transact(transactor).unsafeRunSync()

    println(select)

  }
}