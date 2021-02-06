package com.mikerusoft.citizens.model

case class Phone(value: String, `type`: PhoneType) {
  def toBuilder(): Phone.Builder = {
    new Phone.Builder(Option(value), Option(`type`))
  }
}

object Phone {
  val HOME_TYPE = new HomePhoneType
  val MOBILE_TYPE = new MobilePhoneType
  val WORK_TYPE = new WorkPhoneType

  def builder(): Builder = new Builder()

  class Builder(var value: Option[String], var `type`: Option[PhoneType]) {
    def this() = this(None, None)

    def value(value: String): Builder = { this.value = Option(value); this }
    def `type`(`type`: PhoneType): Builder = { this.`type` = Option(`type`); this }

    def build(): Phone = new Phone(value.get, `type`.get)
  }
}

trait PhoneType {}
sealed class HomePhoneType extends PhoneType
sealed class MobilePhoneType extends PhoneType
sealed class WorkPhoneType extends PhoneType


