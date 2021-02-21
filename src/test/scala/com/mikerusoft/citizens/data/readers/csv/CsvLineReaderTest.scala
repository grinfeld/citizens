package com.mikerusoft.citizens.data.readers.csv

import com.mikerusoft.citizens.model.Phone
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CsvLineReaderTest extends AnyFlatSpec with Matchers {

  "when only firstName and lastName" should "expected Person with only first name and lastname and remove false, all others are None or empty" in {
    val reader = new CsvLineReader(List((0, new FirstName), (1, new LastName)).toMap, ",")
    val person = reader.readLine("Misha, Grinfeld")
    assertResult(person.personalInfo.firstName)("Misha")
    assertResult(person.personalInfo.lastName)("Grinfeld")
    assertResult(person.tz)(None)
    assertResult(person.address)(None)
    assertResult(person.emails)(List())
    assertResult(person.phones)(List())
    assertResult(person.remove)(false)
    assertResult(person.tags)(List())
  }

  "when First Name and Last Name in different places and Tz less than 9 digits" should "expected Person with first, last names and Tz with 0 followed by original value" in {
    val reader = new CsvLineReader(
      List(
        (0, new FirstName),
        (1, new Tz),
        (2, new LastName)
      ).toMap, ",")
    val person = reader.readLine("Misha,12345678, Grinfeld")
    assertResult(person.personalInfo.firstName)("Misha")
    assertResult(person.personalInfo.lastName)("Grinfeld")
    assertResult(person.tz)(Some("012345678"))
    assertResult(person.address)(None)
    assertResult(person.emails)(List())
    assertResult(person.phones)(List())
    assertResult(person.remove)(false)
    assertResult(person.tags)(List())
  }

  "when First Name and Last Name and phone" should "expected Person with first, last names and phone in international form" in {
    val reader = new CsvLineReader(
      List(
        (0, new FirstName),
        (1, new MobilePhoneHeader("972", "0")),
        (2, new LastName)
      ).toMap, ",")
    val person = reader.readLine("Misha,(054)4403945, Grinfeld")
    assertResult(person.personalInfo.firstName)("Misha")
    assertResult(person.personalInfo.lastName)("Grinfeld")
    assertResult(person.tz)(None)
    assertResult(person.address)(None)
    assertResult(person.emails)(List())
    assertResult(person.phones)(List(Phone("972544403945", Phone.MOBILE_TYPE)))
    assertResult(person.remove)(false)
    assertResult(person.tags)(List())
  }

  "when First Name and Last Name and tags" should "expected Person with first, last names and rags as list" in {
    val reader = new CsvLineReader(
      List(
        (0, new FirstName),
        (1, new Tags("\\|")),
        (2, new LastName)
      ).toMap, ",")
    val person = reader.readLine("Misha, one|two|three, Grinfeld")
    assertResult(person.personalInfo.firstName)("Misha")
    assertResult(person.personalInfo.lastName)("Grinfeld")
    assertResult(person.tz)(None)
    assertResult(person.address)(None)
    assertResult(person.emails)(List())
    assertResult(person.phones)(List())
    assertResult(person.remove)(false)
    assertResult(person.tags)(List("one", "two","three"))
  }

  "when Full Name (first name first)" should "expected Person with only first name and lastname and remove false, all others are None or empty" in {
    val reader = new CsvLineReader(List((0, new FullNameFirstNameFirst())).toMap, ",")
    val person = reader.readLine("Misha Grinfeld")
    assertResult(person.personalInfo.firstName)("Misha")
    assertResult(person.personalInfo.lastName)("Grinfeld")
    assertResult(person.tz)(None)
    assertResult(person.address)(None)
    assertResult(person.emails)(List())
    assertResult(person.phones)(List())
    assertResult(person.remove)(false)
    assertResult(person.tags)(List())
  }

  "when Full Name (last name first)" should "expected Person with only first name and lastname and remove false, all others are None or empty" in {
    val reader = new CsvLineReader(List((0, new FullNameLastNameFirst("\\|"))).toMap, ",")
    val person = reader.readLine("Grinfeld|Misha")
    assertResult(person.personalInfo.firstName)("Misha")
    assertResult(person.personalInfo.lastName)("Grinfeld")
    assertResult(person.tz)(None)
    assertResult(person.address)(None)
    assertResult(person.emails)(List())
    assertResult(person.phones)(List())
    assertResult(person.remove)(false)
    assertResult(person.tags)(List())
  }
}