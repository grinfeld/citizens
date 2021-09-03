package com.mikerusoft.citizens.model

import cats.data.Validated
import cats.implicits.catsSyntaxTuple2Semigroupal
import com.mikerusoft.citizens.model.Person.FilterBlankString
import cats.syntax.option._
import com.mikerusoft.citizens.model.Types.ErrorMsg
import com.mikerusoft.citizens.model.context.Validation._

case class Phone(id: Option[Long], personId: Option[Long], value: String, `type`: PhoneType)

object Phone {

  val HOME_TYPE = new HomePhoneType
  val MOBILE_TYPE = new MobilePhoneType
  val WORK_TYPE = new WorkPhoneType

  def builder(): Builder = Builder(None, None, None, None)

  case class Builder private[Phone](var id: Option[Long], var personId: Option[Long], var value: Option[String], var `type`: Option[PhoneType]) {

    def value(value: String): Builder = { copy(value = Option(value).filterNotEmpty()) }
    def `type`(`type`: PhoneType): Builder = { copy(`type` = Option(`type`)) }
    def id(id: Long): Builder = { copy(id = Option(id)) }
    def personId(personId: Long): Builder = { copy(personId = Option(personId)) }

    def buildWith() : Validated[ErrorMsg, Phone] = {
      (value.toValid("Invalid phone value"), `type`.toValid("Invalid phone type")).mapN((p, t) => new Phone(None, None, p, t))
    }
  }
}

trait PhoneType {}
sealed case class HomePhoneType() extends PhoneType
sealed case class MobilePhoneType() extends PhoneType
sealed case class WorkPhoneType() extends PhoneType


