package com.mikerusoft.citizens.infra

import com.mikerusoft.citizens.data.outputs.DataOutput
import com.mikerusoft.citizens.data.parsers.csv._
import com.mikerusoft.citizens.model.Person
import com.mikerusoft.citizens.model.Types.{Invalid, Valid, Validation}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


class ValidatedWithEffectTest extends AnyFlatSpec with Matchers {

  "when valid input and output" should "expected number of Valid records" in {
    val reader = CsvLineParser(List((0, FirstName()), (1, LastName()), (2, MobilePhoneHeader("972", "0"))).toMap)
    val skipHeader = true
    val outputValid: DataOutput[Person, String] = p => Valid(s"${p}")

    val result: Validation[Int] =
      ValidatedWithEffect(CsvFileReader.parseLines(skipHeader, List("Misha,Grinfeld, 0544403945", "Nir,Orfly,0544403945", "Ilya,Morgenshtern,0544403945", "Igor,Grinfeld,0544403945").iterator)(reader))
        .map(parsedPersons => parsedPersons.mapLine(p => outputValid.outputTo(p)))
        .map(validatedPersons => validatedPersons.map {
          case Valid(_) => Valid(1)
          case Invalid(e) => Invalid(e).asInstanceOf[Validation[Int]]
        })
        .foldM(0)((first, second:Int) => first + second)(it => it)
        .run
    println(result)
    assert(result == Valid(4))
  }

  "when invalid phone and valid output" should "expected Invalid" in {
    val reader = CsvLineParser(List((0, FirstName()), (1, LastName()), (2, MobilePhoneHeader("972", "0"))).toMap)
    val skipHeader = true
    val outputValid: DataOutput[Person, String] = p => Valid(s"${p}")

    val result: Validation[Int] =
      ValidatedWithEffect(CsvFileReader.parseLines(skipHeader, List("Misha,Grinfeld, 0544403945", "Nir,Orfly,dgs453", "Ilya,Morgenshtern,gsd46", "Igor,Grinfeld,fdshgf54").iterator)(reader))
        .map(parsedPersons => parsedPersons.mapLine(p => outputValid.outputTo(p)))
        .map(validatedPersons => validatedPersons.map {
          case Valid(_) => Valid(1)
          case Invalid(e) => Invalid(e).asInstanceOf[Validation[Int]]
        })
        .foldM(0)((first, second:Int) => first + second)(it => it)
        .run

    println(result)
    result match {
      case Valid(e) => fail(s"Shoud be invalid, but $e")
      case Invalid(_) =>
    }
  }
}
