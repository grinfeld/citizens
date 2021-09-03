package com.mikerusoft.citizens.data.parsers.csv

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import com.mikerusoft.citizens.model.{Person, Phone}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CsvLineParserTest extends AnyFlatSpec with Matchers {

  private def assertValidValue(runFunc: () => Validated[String, Person]): Person = {
    runFunc() match {
      case Valid(person) => person
      case Invalid(e) => fail(e)
    }
  }

  private def assertInvalidValue(runFunc: () => Validated[String, Person], msg: String = ""): Unit = {
    runFunc() match {
      case Valid(person) => fail(s"Got person $person")
      case Invalid(e) =>
        if (!msg.isBlank)
          assert(msg == e)
    }
  }

  "when no headers" should "expected no required first name and last name error message" in {
    val reader = CsvLineParser(List().toMap)
    assertInvalidValue(() => reader.readLine("Misha,Grinfeld"), "Empty first name, Empty last name")
  }

  "when only first name" should "expected no required last name error message" in {
    val reader = CsvLineParser(List((0, new FirstName), (1, new LastName)).toMap)
    assertInvalidValue(() => reader.readLine("Misha,,"), "Empty last name")
  }

  "when invalid mobile phone" should "expected invalid phone value error message" in {
    val reader = CsvLineParser(List(
            (0, FirstName()),
            (1, MobilePhoneHeader("972", "0")),
            (2, LastName())
          ).toMap)
    assertInvalidValue(() => reader.readLine("   Misha,fgdfgdf, Grinfeld"), "Invalid phone value")
  }

  "without quotes when only firstName and lastName" should "expected Person with only first name and lastname and remove false, all others are None or empty" in {
    val reader = CsvLineParser(List((0, new FirstName), (1, new LastName)).toMap)
    val person = assertValidValue(() => reader.readLine("Misha,Grinfeld"))
    assertResult("Misha")(person.personalInfo.firstName)
    assertResult("Grinfeld")(person.personalInfo.lastName)
    assertResult(None)(person.tz)
    assertResult(None)(person.address)
    assertResult(List())(person.emails)
    assertResult(List())(person.phones)
    assertResult(false)(person.remove)
    assertResult(List())(person.tags)
  }

  "with quotes when only firstName and lastName" should "expected Person with only first name and lastname and remove false, all others are None or empty" in {
    val reader = CsvLineParser(List((0, new FirstName), (1, new LastName)).toMap)
    val person = assertValidValue(() => reader.readLine("'Misha', 'Grinfeld'"))
    assertResult("Misha")(person.personalInfo.firstName)
    assertResult("Grinfeld")(person.personalInfo.lastName)
    assertResult(None)(person.tz)
    assertResult(None)(person.address)
    assertResult(List())(person.emails)
    assertResult(List())(person.phones)
    assertResult(false)(person.remove)
    assertResult(List())(person.tags)
  }

  "without quotes and quote in the middle of name when only firstName and lastName" should "expected Person with only first name and lastname and remove false, all others are None or empty" in {
    val reader = CsvLineParser(List((0, new FirstName), (1, new LastName)).toMap)
    val person = assertValidValue(() => reader.readLine("Mi'sha, Grinfeld"))
    assertResult("Mi'sha")(person.personalInfo.firstName)
    assertResult("Grinfeld")(person.personalInfo.lastName)
    assertResult(None)(person.tz)
    assertResult(None)(person.address)
    assertResult(List())(person.emails)
    assertResult(List())(person.phones)
    assertResult(false)(person.remove)
    assertResult(List())(person.tags)
  }

  "with quotes and comma in the middle of name when only firstName and lastName" should "expected Person with only first name and lastname and remove false, all others are None or empty" in {
    val reader = CsvLineParser(List((0, new FirstName), (1, new LastName)).toMap)
    val person = assertValidValue(() => reader.readLine("'Mi,sha', Grinfeld"))
    assertResult("Mi,sha")(person.personalInfo.firstName)
    assertResult("Grinfeld")(person.personalInfo.lastName)
    assertResult(None)(person.tz)
    assertResult(None)(person.address)
    assertResult(List())(person.emails)
    assertResult(List())(person.phones)
    assertResult(false)(person.remove)
    assertResult(List())(person.tags)
  }

  "without quotes and First name, Last name and empty field in the middle in the middle" should "expected Person with only first name and lastname and remove false, all others are None or empty" in {
    val reader = CsvLineParser(List((0, new FirstName),(1, new MiddleName), (2, new LastName)).toMap)
    val person = assertValidValue(() => reader.readLine("Misha,,Grinfeld"))
    assertResult("Misha")(person.personalInfo.firstName)
    assertResult("Grinfeld")(person.personalInfo.lastName)
    assertResult(None)(person.personalInfo.middleName)
    assertResult(None)(person.tz)
    assertResult(None)(person.address)
    assertResult(List())(person.emails)
    assertResult(List())(person.phones)
    assertResult(false)(person.remove)
    assertResult(List())(person.tags)
  }

  "when First Name and Last Name in different places and Tz less than 9 digits" should "expected Person with first, last names and Tz with 0 followed by original value" in {
    val reader = CsvLineParser(List(
            (0,  FirstName()),
            (1, Tz()),
            (2, LastName())
          ).toMap)
    val person = assertValidValue(() => reader.readLine("Misha   ,   12345678    , Grinfeld   "))
    assertResult(person.personalInfo.firstName)("Misha")
    assertResult(person.personalInfo.lastName)("Grinfeld")
    assertResult(person.tz)(Some("012345678"))
    assertResult(None)(person.address)
    assertResult(List())(person.emails)
    assertResult(List())(person.phones)
    assertResult(false)(person.remove)
    assertResult(List())(person.tags)
  }

  "when First Name and Last Name and phone" should "expected Person with first, last names and phone in international form" in {
    val reader = CsvLineParser(List(
            (0, new FirstName),
            (1, new MobilePhoneHeader("972", "0")),
            (2, new LastName)
          ).toMap)
    val person = assertValidValue(() => reader.readLine("   Misha,(054)4403945, Grinfeld"))
    assertResult(person.personalInfo.firstName)("Misha")
    assertResult(person.personalInfo.lastName)("Grinfeld")
    assertResult(None)(person.tz)
    assertResult(None)(person.address)
    assertResult(List())(person.emails)
    assertResult(person.phones)(List(Phone(None, None, "972544403945", Phone.MOBILE_TYPE)))
    assertResult(false)(person.remove)
    assertResult(List())(person.tags)
  }

  "when First Name and Last Name and tags" should "expected Person with first, last names and rags as list" in {
    val reader = CsvLineParser(List(
            (0, new FirstName),
            (1, new Tags("\\|")),
            (2, new LastName)
          ).toMap)
    val person = assertValidValue(() => reader.readLine("Misha, one|two|three, Grinfeld"))
    assertResult(person.personalInfo.firstName)("Misha")
    assertResult(person.personalInfo.lastName)("Grinfeld")
    assertResult(None)(person.tz)
    assertResult(None)(person.address)
    assertResult(List())(person.emails)
    assertResult(List())(person.phones)
    assertResult(false)(person.remove)
    assertResult(person.tags)(List("one", "two","three"))
  }

  "when Full Name (first name first)" should "expected Person with only first name and lastname and remove false, all others are None or empty" in {
    val reader = CsvLineParser(List((0, FullNameFirstNameFirst())).toMap)
    val person = assertValidValue(() => reader.readLine("Misha Grinfeld"))
    assertResult(person.personalInfo.firstName)("Misha")
    assertResult(person.personalInfo.lastName)("Grinfeld")
    assertResult(None)(person.tz)
    assertResult(None)(person.address)
    assertResult(List())(person.emails)
    assertResult(List())(person.phones)
    assertResult(false)(person.remove)
    assertResult(List())(person.tags)
  }

  "when Full Name (last name first)" should "expected Person with only first name and lastname and remove false, all others are None or empty" in {
    val reader = CsvLineParser(List((0, FullNameLastNameFirst("\\|"))).toMap)
    val person = assertValidValue(() => reader.readLine("Grinfeld|Misha"))
    assertResult(person.personalInfo.firstName)("Misha")
    assertResult(person.personalInfo.lastName)("Grinfeld")
    assertResult(None)(person.tz)
    assertResult(None)(person.address)
    assertResult(List())(person.emails)
    assertResult(List())(person.phones)
    assertResult(false)(person.remove)
    assertResult(List())(person.tags)
  }
}
