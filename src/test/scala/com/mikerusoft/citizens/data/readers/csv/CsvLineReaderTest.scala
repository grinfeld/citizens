package com.mikerusoft.citizens.data.readers.csv

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CsvLineReaderTest extends AnyFlatSpec with Matchers {

  "when only firstName and lastName" should "get Person with only first name and lastname and remove false, all others are None or empty" in {
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
}
