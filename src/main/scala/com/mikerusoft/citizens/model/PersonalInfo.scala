package com.mikerusoft.citizens.model

case class PersonalInfo(firstName: String, lastName: String, middleName: Option[String], bornYear: Option[Int])

object PersonalInfo {

  def builder(): Builder = new Builder()

  class Builder(var firstName: Option[String], var lastName: Option[String], var middleName: Option[String], var bornYear: Option[Int]) {
    def this() = this(None, None, None, None)

    def firstName(firstName: String): Builder = { this.firstName = Option(firstName); this }
    def lastName(lastName: String): Builder = { this.lastName = Option(lastName); this }
    def middleName(middleName: String): Builder = { this.middleName = Option(middleName); this }
    def bornYear(bornYear: Int): Builder = { this.bornYear = Option(bornYear); this }

    def build():PersonalInfo = new PersonalInfo(firstName.get, lastName.get, middleName, bornYear)
  }
}

