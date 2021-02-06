package com.mikerusoft.citizens.model

case class Phone(value: String, `type`: PhoneType)

object Phone {
  val HOME_TYPE = new HomePhoneType
  val MOBILE_TYPE = new MobilePhoneType
  val WORK_TYPE = new WorkPhoneType
}

trait PhoneType {}
sealed class HomePhoneType extends PhoneType
sealed class MobilePhoneType extends PhoneType
sealed class WorkPhoneType extends PhoneType

