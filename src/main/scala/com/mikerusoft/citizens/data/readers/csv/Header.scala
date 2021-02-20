package com.mikerusoft.citizens.data.readers.csv

import com.mikerusoft.citizens.model.{Phone, PhoneType}

import java.time.format.DateTimeFormatter

trait Header
sealed class Tz extends Header
sealed class FullNameFirstNameFirst(val delimiter: String = " ") extends Header

sealed class FullNameLastNameFirst(val delimiter: String = " ") extends Header
sealed class FirstName extends Header
sealed class LastName extends Header
sealed class MiddleName extends Header
sealed class Age extends Header
sealed class BornYear extends Header
sealed class BirthDay(val _dateFormat: String) extends Header {
  val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern(_dateFormat)
}
sealed class City extends Header
sealed class Street extends Header
sealed class BuildingNo extends Header
sealed class ApartmentNo extends Header
sealed class Entrance extends Header
sealed class NeighborhoodName extends Header
abstract class PhoneNumber(val countryToUse: String, val localPrefix: String, val phoneType: PhoneType) extends Header
sealed class MobilePhoneHeader(override val countryToUse: String, override val localPrefix: String)
                          extends PhoneNumber(countryToUse, localPrefix, phoneType = Phone.MOBILE_TYPE)
sealed class WorkPhoneHeader(override val countryToUse: String, override val localPrefix: String)
                          extends PhoneNumber(countryToUse, localPrefix, phoneType = Phone.WORK_TYPE)
sealed class HomePhoneHeader(override val countryToUse: String, override val localPrefix: String)
                          extends PhoneNumber(countryToUse, localPrefix, phoneType = Phone.HOME_TYPE)
sealed class Email extends Header
sealed class Tags(val delimiter: String = ",") extends Header
sealed class Remove extends Header