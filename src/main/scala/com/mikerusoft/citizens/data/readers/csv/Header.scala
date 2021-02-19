package com.mikerusoft.citizens.data.readers.csv

import com.mikerusoft.citizens.model.{PersonalInfo, Phone, PhoneType}

import java.time.format.DateTimeFormatter

trait Header[T]
sealed class Tz extends Header[String]
sealed class FullNameFirstNameFirst(val delimiter: String = " ") extends Header[PersonalInfo.Builder]

sealed class FullNameLastNameFirst(val delimiter: String = " ") extends Header[PersonalInfo.Builder]
sealed class FirstName extends Header[String]
sealed class LastName extends Header[String]
sealed class MiddleName extends Header[String]
sealed class Age extends Header[Int]
sealed class BornYear extends Header[Int]
sealed class BirthDay(val _dateFormat: String) extends Header[Int] {
  val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern(_dateFormat)
}
sealed class City extends Header[String]
sealed class Street extends Header[String]
sealed class BuildingNo extends Header[String]
sealed class ApartmentNo extends Header[String]
sealed class Entrance extends Header[String]
sealed class NeighborhoodName extends Header[String]
abstract class PhoneNumber(val countryToUse: String, val localPrefix: String, val phoneType: PhoneType) extends Header[Phone.Builder]
sealed class MobilePhoneHeader(override val countryToUse: String, override val localPrefix: String)
                          extends PhoneNumber(countryToUse, localPrefix, phoneType = Phone.MOBILE_TYPE)
sealed class WorkPhoneHeader(override val countryToUse: String, override val localPrefix: String)
                          extends PhoneNumber(countryToUse, localPrefix, phoneType = Phone.WORK_TYPE)
sealed class HomePhoneHeader(override val countryToUse: String, override val localPrefix: String)
                          extends PhoneNumber(countryToUse, localPrefix, phoneType = Phone.HOME_TYPE)
sealed class Email extends Header[String]
sealed class Tags(val delimiter: String = ",") extends Header[List[String]]
sealed class Remove extends Header[Boolean]