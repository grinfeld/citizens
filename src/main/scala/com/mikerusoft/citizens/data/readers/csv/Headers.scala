package com.mikerusoft.citizens.data.readers.csv

import com.mikerusoft.citizens.data.readers.csv.Headers.StringToOpt
import com.mikerusoft.citizens.model.{Phone, PhoneType}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.{ChronoField, ChronoUnit}
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

object Headers {
  implicit class StringToOpt(value: String) {
    def tryIt[T](func: String => T):Option[T] = {
      Try(func(value)) match {
        case Success(v) => Option(v)
        case Failure(_) => None
      }
    }
  }
}

trait Headers[+T] {
  protected def toValue(value: String): Option[T]
}

abstract class IntHeader extends Headers[Int] {
  override def toValue(value: String): Option[Int] = value.tryIt( _.trim.toInt)
}

abstract class LongHeader extends Headers[Long] {
  override def toValue(value: String): Option[Long] = value.tryIt(_.trim.toLong)
}

abstract class StringHeader extends Headers[String] {
  override def toValue(value: String): Option[String] = Option(value)
}

sealed class Tz extends Headers[String] {
  override def toValue(value: String): Option[String] = {
    @tailrec
    def appendWithZeros(value: String): String = if (value.length >= 9) value else appendWithZeros("0" + value)

    Option(value).flatMap(v => v.tryIt(_.trim.toLong.toString))
      .filter(v => v.length <= 9).map(v => appendWithZeros(v))
  }
}
sealed class FullName extends StringHeader
sealed class FirstName extends StringHeader
sealed class LastName extends StringHeader
sealed class MiddleName extends StringHeader
sealed class Age extends IntHeader {
  override def toValue(value: String): Option[Int] = {
    super.toValue(value).map(age => LocalDateTime.now().minus(age, ChronoUnit.YEARS))
      .map(dt => dt.get(ChronoField.YEAR)).filter(t => t > 1900)
  }
}
sealed class BornYear extends IntHeader
sealed class BirthDay(var _dateFormat: String) extends Headers[Int] {
  private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern(_dateFormat)
  override def toValue(value: String): Option[Int] = {
    Option(value).map(_.trim).map(date => dateFormat.parse(date))
      .map(dt => dt.get(ChronoField.YEAR)).filter(t => t > 1900)
  }
}
sealed class City extends StringHeader
sealed class FullAddress extends StringHeader
sealed class Street extends StringHeader
sealed class BuildingNo extends StringHeader
sealed class ApartmentNo extends StringHeader
sealed class Entrance extends StringHeader
sealed class NeighborhoodName extends StringHeader
abstract class PhoneNumber(val countryToUse: String, val localPrefix: String, val phoneType: PhoneType) extends Headers[Phone] {
  override def toValue(value: String): Option[Phone] = {
    value.tryIt(_.trim.replaceAll("[\\s\\-\\(\\)]", "").toLong.toString) match {
      case Some(phoneStringValue) =>
        val phoneNum = if (phoneStringValue.startsWith(countryToUse)) {
            phoneStringValue
          } else if (phoneStringValue.startsWith(localPrefix)){
            countryToUse + phoneStringValue.substring(1)
          } else {
            countryToUse + phoneStringValue
          }
        Option(Phone(phoneNum, phoneType))
      case None => None
    }
  }
}
sealed class MobilePhone(override val countryToUse: String, override val localPrefix: String)
                          extends PhoneNumber(countryToUse, localPrefix, phoneType = Phone.MOBILE_TYPE)
sealed class WorkPhone(override val countryToUse: String, override val localPrefix: String)
                          extends PhoneNumber(countryToUse, localPrefix, phoneType = Phone.WORK_TYPE)
sealed class HomePhone(override val countryToUse: String, override val localPrefix: String)
                          extends PhoneNumber(countryToUse, localPrefix, phoneType = Phone.HOME_TYPE)
sealed class Email extends StringHeader
sealed class Tags(val delimiter: String) extends Headers[List[String]] {
  override def toValue(value: String): Option[List[String]] = value.tryIt(_.trim.split(delimiter).toList)
}