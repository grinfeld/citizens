package com.mikerusoft.citizens.model

import cats.data.Validated
import cats.implicits.catsSyntaxTuple2Semigroupal
import com.mikerusoft.citizens.model.Person.FilterBlankString
import cats.syntax.option._
import com.mikerusoft.citizens.model.Types.ErrorMsg
import com.mikerusoft.citizens.model.context.Validation._

case class Phone(id: Option[Long], personId: Option[Long], value: String, `type`: PhoneType) {
  def toBuilder(): Phone.Builder = {
    new Phone.Builder(None, None, Option(value), Option(`type`))
  }
}

object Phone {

  val HOME_TYPE = new HomePhoneType
  val MOBILE_TYPE = new MobilePhoneType
  val WORK_TYPE = new WorkPhoneType

  def builder(): Builder = new Builder()

  class Builder(var id: Option[Long], var personId: Option[Long], var value: Option[String], var `type`: Option[PhoneType]) {
    def this() = this(None, None, None, None)

    def value(value: String): Builder = { this.value = Option(value).filterNotEmpty(); this }
    def `type`(`type`: PhoneType): Builder = { this.`type` = Option(`type`); this }
    def id(id: Long): Builder = { this.id = Option(id); this }
    def personId(personId: Long): Builder = { this.personId = Option(personId); this }

    def buildWith() : Validated[ErrorMsg, Phone] = {
      (value.toValid("Invalid phone value"), `type`.toValid("Invalid phone type")).mapN((p, t) => new Phone(None, None, p, t))
    }
  }
}

trait PhoneType {}
sealed case class HomePhoneType() extends PhoneType
sealed case class MobilePhoneType() extends PhoneType
sealed case class WorkPhoneType() extends PhoneType


