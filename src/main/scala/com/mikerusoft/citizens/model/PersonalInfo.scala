package com.mikerusoft.citizens.model

import cats.data.Validated
import cats.implicits.catsSyntaxTuple2Semigroupal
import com.mikerusoft.citizens.model.Person.FilterBlankString
import cats.syntax.option._
import com.mikerusoft.citizens.model.Types.ErrorMsg
import com.mikerusoft.citizens.model.context.Validation._

case class PersonalInfo(personId: Option[Long], firstName: String, lastName: String, middleName: Option[String], bornYear: Option[Int])

object PersonalInfo {

  def builder(): Builder = new Builder()

  class Builder(var personId: Option[Long], var firstName: Option[String], var lastName: Option[String], var middleName: Option[String], var bornYear: Option[Int]) {
    def this() = this(None, None, None, None, None)

    def withFirstName(firstName: String): Builder = { this.firstName = Option(firstName).filterNotEmpty(); this }
    def firstName(firstName: Option[String]): Builder = { this.firstName = firstName.filterNotEmpty(); this }
    def withLastName(lastName: String): Builder = { this.lastName = Option(lastName).filterNotEmpty(); this }
    def lastName(lastName: Option[String]): Builder = { this.lastName = lastName.filterNotEmpty(); this }
    def withMiddleName(middleName: String): Builder = { this.middleName = Option(middleName).filterNotEmpty(); this }
    def bornYear(bornYear: Int): Builder = { this.bornYear = Option(bornYear); this }
    def personId(personId: Long): Builder = { this.personId = Option(personId); this }

    def buildWith() : Validated[ErrorMsg, PersonalInfo] = {
      (firstName.toValid("Empty first name"), lastName.toValid("Empty last name"))
        .mapN((firstName, lastName) => new PersonalInfo(None, firstName, lastName, middleName, bornYear))
    }
  }
}

