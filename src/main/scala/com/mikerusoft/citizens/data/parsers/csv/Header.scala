package com.mikerusoft.citizens.data.parsers.csv

import com.mikerusoft.citizens.model.{Phone, PhoneType}

import java.time.format.DateTimeFormatter

sealed trait Header
final case class Tz() extends Header
final case class FullNameFirstNameFirst(delimiter: String = " ") extends Header

final case class FullNameLastNameFirst(delimiter: String = " ") extends Header
final case class FirstName() extends Header
final case class LastName() extends Header
final case class MiddleName() extends Header
final case class Age() extends Header
final case class BornYear() extends Header
final case class BirthDay(_dateFormat: String) extends Header {
  val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern(_dateFormat)
}
final case class City() extends Header
final case class Street() extends Header
final case class BuildingNo() extends Header
final case class ApartmentNo() extends Header
final case class Entrance() extends Header
final case class NeighborhoodName() extends Header
sealed abstract class PhoneNumberHeader(val countryToUse: String, val localPrefix: String, val phoneType: PhoneType) extends Header
final case class MobilePhoneHeader(override val countryToUse: String, override val localPrefix: String)
                          extends PhoneNumberHeader(countryToUse, localPrefix, phoneType = Phone.MOBILE_TYPE)
final case class WorkPhoneHeader(override val countryToUse: String, override val localPrefix: String)
                          extends PhoneNumberHeader(countryToUse, localPrefix, phoneType = Phone.WORK_TYPE)
final case class HomePhoneHeader(override val countryToUse: String, override val localPrefix: String)
                          extends PhoneNumberHeader(countryToUse, localPrefix, phoneType = Phone.HOME_TYPE)
final case class Email() extends Header
final case class Tags(delimiter: String = ",") extends Header
final case class Remove() extends Header