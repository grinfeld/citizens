package com.mikerusoft.citizens.model

import cats.data.Validated
import cats.implicits.catsSyntaxTuple2Semigroupal
import com.mikerusoft.citizens.model.Person.FilterBlankString
import cats.syntax.option._
import com.mikerusoft.citizens.model.Types.ErrorMsg
import com.mikerusoft.citizens.model.context.Validation._

case class PersonalInfo(personId: Option[Long], firstName: String, lastName: String, middleName: Option[String], bornYear: Option[Int])

object PersonalInfo {

  def builder(): Builder = Builder(None, None, None, None, None)

  case class Builder(var personId: Option[Long], var firstName: Option[String], var lastName: Option[String], var middleName: Option[String], var bornYear: Option[Int]) {

    def withFirstName(firstName: String): Builder = { copy(firstName = Option(firstName).filterNotEmpty())}
    def firstName(firstName: Option[String]): Builder = { copy(firstName = firstName.filterNotEmpty()) }
    def withLastName(lastName: String): Builder = { copy(lastName = Option(lastName).filterNotEmpty()) }
    def lastName(lastName: Option[String]): Builder = { copy(lastName = lastName.filterNotEmpty()) }
    def withMiddleName(middleName: String): Builder = { copy(middleName = Option(middleName).filterNotEmpty()) }
    def bornYear(bornYear: Int): Builder = { copy(bornYear = Option(bornYear)) }
    def personId(personId: Long): Builder = { copy(personId = Option(personId)) }

    def buildWith() : Validated[ErrorMsg, PersonalInfo] = {
      (firstName.toValid("Empty first name"), lastName.toValid("Empty last name"))
        .mapN((firstName, lastName) => new PersonalInfo(None, firstName, lastName, middleName, bornYear))
    }
  }
}

