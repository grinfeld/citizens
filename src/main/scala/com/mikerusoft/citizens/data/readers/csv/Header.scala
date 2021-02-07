package com.mikerusoft.citizens.data.readers.csv

import com.mikerusoft.citizens.data.readers.csv.Header.StringToOpt
import com.mikerusoft.citizens.model.{PersonalInfo, Phone, PhoneType}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.{ChronoField, ChronoUnit}
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

object Header {
  implicit class StringToOpt(value: String) {
    def tryIt[T](func: String => T):Option[T] = {
      Try(func(value)) match {
        case Success(v) => Option(v)
        case Failure(_) => None
      }
    }
  }
}

trait Header[T] {
  def toValue(value: String): Option[T]
  def toValue[F](value: String, func: T => F)(f: F): F = {
    toValue(value) match {
      case Some(v) => func.apply(v)
      case None => f
    }
  }
}

abstract class IntHeader extends Header[Int] {
  override def toValue(value: String): Option[Int] = value.tryIt( _.toInt)
}

abstract class LongHeader extends Header[Long] {
  override def toValue(value: String): Option[Long] = value.tryIt(_.toLong)
}

abstract class StringHeader extends Header[String] {
  override def toValue(value: String): Option[String] = Option(value)
}

sealed class Tz extends Header[String] {
  override def toValue(value: String): Option[String] = {
    @tailrec
    def appendWithZeros(value: String): String = if (value.length >= 9) value else appendWithZeros("0" + value)

    Option(value).flatMap(v => v.tryIt(_.toLong.toString))
      .filter(v => v.length <= 9).map(v => appendWithZeros(v))
  }
}
sealed class FullNameFirstNameFirst(val delimiter: String = "") extends Header[PersonalInfo.Builder] {
  override def toValue(value: String): Option[PersonalInfo.Builder] = {
    value.split(delimiter).toList match {
      case Nil => None
      case header :: Nil => Option(PersonalInfo.builder().firstName(header))
      case header :: remainder => Option(PersonalInfo.builder().firstName(header).lastName(remainder.mkString(" ")))
    }
  }
}
sealed class FullNameLastNameFirst(val delimiter: String = "") extends Header[PersonalInfo.Builder] {
  override def toValue(value: String): Option[PersonalInfo.Builder] = {
    value.split(delimiter).toList match {
      case Nil => None
      case header :: Nil => Option(PersonalInfo.builder().lastName(header))
      case header :: remainder => Option(PersonalInfo.builder().lastName(header).firstName(remainder.mkString(" ")))
    }
  }
}
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
sealed class BirthDay(var _dateFormat: String) extends Header[Int] {
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
abstract class PhoneNumber(val countryToUse: String, val localPrefix: String, val phoneType: PhoneType) extends Header[Phone.Builder] {
  override def toValue(value: String): Option[Phone.Builder] = {
    value.tryIt(_.replaceAll("[\\s\\-\\(\\)]", "").toLong.toString) match {
      case Some(phoneStringValue) =>
        val phoneNum = if (phoneStringValue.startsWith(countryToUse)) {
            phoneStringValue
          } else if (phoneStringValue.startsWith(localPrefix)){
            countryToUse + phoneStringValue.substring(1)
          } else {
            countryToUse + phoneStringValue
          }
        Option(Phone.builder().value(phoneNum).`type`(phoneType))
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
sealed class Tags(val delimiter: String) extends Header[List[String]] {
  override def toValue(value: String): Option[List[String]] = value.tryIt(_.split(delimiter).toList)
}
sealed class Remove extends Header[Boolean] {
  override def toValue(value: String): Option[Boolean] =
    Option(if (value.isBlank || value.equals("") || !value.equalsIgnoreCase("true") || value.equals("0")) false else true)
}