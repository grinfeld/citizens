package com.mikerusoft.citizens.model

import cats.data.Validated
import cats.implicits.catsSyntaxTuple2Semigroupal
import com.mikerusoft.citizens.model.Person.FilterBlankString
import cats.syntax.option._
import com.mikerusoft.citizens.model.PhoneTypes.PhoneType
import com.mikerusoft.citizens.model.Types.ErrorMsg
import com.mikerusoft.citizens.model.context.Validation._


object PhoneTypes extends Enumeration {
  type PhoneType = Value

  val home: PhoneTypes.Value = Value(0, "home")
  val mobile: PhoneTypes.Value = Value(1, "mobile")
  val work: PhoneTypes.Value = Value(2, "work")
}

case class Phone(id: Option[Long], personId: Option[Long], value: String, `type`: PhoneType)

object Phone {

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
  def apply(id: Option[Long], personId: Option[Long], value: String, `type`: String): Phone = {
    val tp = `type` match {
      case "home" => PhoneTypes.home
      case "mobile" => PhoneTypes.mobile
      case "work" => PhoneTypes.work
    }
    new Phone(id, personId, value, tp)
  }
}


