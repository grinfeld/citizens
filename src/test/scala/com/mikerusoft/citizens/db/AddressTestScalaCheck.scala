package com.mikerusoft.citizens.db

import cats.data.Validated.Valid
import cats.effect.{IO, _}
import com.mikerusoft.citizens.model.Address
import doobie.Transactor
import doobie.implicits._
import io.getquill.{Literal, mirrorContextWithQueryProbing}
import io.getquill.mirrorContextWithQueryProbing.{query, quote}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext

//class AddressTestScalaCheck extends AnyFlatSpec with Matchers with doobie.scalatest.IOChecker {
class AddressTestScalaCheck  {


  /*import doobie.quill.DoobieContext

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

  "stam test" should "with doobie and H2" in {

    sql"""
         CREATE TABLE addresses (
            personId int NOT NULL,
            country varchar(256) NOT NULL,
            city varchar(256) NOT NULL,
            street varchar(256) NOT NULL,
            buildingNo  varchar(10),
            apartment  int,
            entrance varchar(10),
            neighborhood varchar(256)
         )
     """.update.run.transact(transactor).unsafeRunSync()


    implicit val dc = new DoobieContext.H2(Literal) // Literal naming scheme
    implicit val intDec = dc.intEncoder
    implicit val insertSchemaMeta = dc.insertMeta[Address](_.personId)

    implicit val querySchemaMeta = dc.schemaMeta[Address]("addresses")

    Address.builder()
      .country("Israel")
      .city("Petah Tikva")
      .street("Aharon Katsenelson")
      .buildingNo("14")
      .apartment(17)
      .neighborhood("Neve Gan")
      .personId(1).buildWith() match {
      case Valid(address) =>
        address match {
          case Some(a) =>
            val u3 = quote {
              query[Address].insert(mirrorContextWithQueryProbing.lift[Address](a))
            }
            mirrorContextWithQueryProbing.run(u3)
        }
    }

    val q1 = quote { query[Address] }
    val select: mirrorContextWithQueryProbing.QueryMirror[Address] = mirrorContextWithQueryProbing.run(q1)

  }*/
}