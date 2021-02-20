package com.mikerusoft.citizens.data.readers.csv

import com.mikerusoft.citizens.model.{Person, PersonalInfo, Phone, PhoneType}

import java.time.LocalDateTime
import java.time.temporal.{ChronoField, ChronoUnit}
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

trait HeaderConverter[T] {
  def convert(header: T, value: String, builder: Person.Builder): Person.Builder
}

object HeaderConverter {

  implicit object RemoveConverter extends HeaderConverter[Remove] {
    override def convert(header: Remove, value: String, builder: Person.Builder): Person.Builder = {
      value.tryIt(s => if (s.isBlank || s.equals("") || !s.equalsIgnoreCase("true") || s.equals("0")) false else true)
        .toBuilder(builder.withRemove)(builder)
    }
  }

  implicit object TzConverter extends HeaderConverter[Tz] {

    @tailrec
    private def appendWithZeros(value: String): String = if (value.length >= 9) value else appendWithZeros("0" + value)

    override def convert(header: Tz, value: String, builder: Person.Builder): Person.Builder = {
      value.tryIt(_.toLong.toString).filter(v => v.length <= 9).map(appendWithZeros).toBuilder(builder.withTz)(builder)
    }
  }

  implicit object FirstNameConverter extends HeaderConverter[FirstName] {
    override def convert(header: FirstName, value: String, builder: Person.Builder): Person.Builder =
      Option(value).toBuilder(v => builder.withPersonalInfo(b => b.withFirstName(v)))(builder)
  }

  implicit object LastNameConverter extends HeaderConverter[LastName] {
    override def convert(header: LastName, value: String, builder: Person.Builder): Person.Builder =
      Option(value).toBuilder(v => builder.withPersonalInfo(b => b.withLastName(v)))(builder)
  }

  implicit object MiddleNameConverter extends HeaderConverter[MiddleName] {
    override def convert(header: MiddleName, value: String, builder: Person.Builder): Person.Builder =
      Option(value).toBuilder(v => builder.withPersonalInfo(b => b.withMiddleName(v)))(builder)
  }

  private def fillFirstAndLastName(builder: Person.Builder, bs: PersonalInfo.Builder): Person.Builder = {
    builder.withPersonalInfo(b => b.firstName(bs.firstName))
    builder.withPersonalInfo(b => b.lastName(bs.lastName))
  }

  implicit object FullNameFirstNameFirstConverter extends HeaderConverter[FullNameFirstNameFirst] {
    override def convert(header: FullNameFirstNameFirst, value: String, builder: Person.Builder): Person.Builder = {
      value.tryIt(_.split(header.delimiter).toList)
        .toBuilder(v => {
          fillFirstAndLastName(builder, v match {
            case Nil => PersonalInfo.builder()
            case header :: Nil => PersonalInfo.builder().withLastName(header)
            case header :: remainder => PersonalInfo.builder().withFirstName(header).withLastName(remainder.mkString(" "))
          })
        })(builder)
    }
  }

  implicit object FullNameLastNameFirstConverter extends HeaderConverter[FullNameLastNameFirst] {
    override def convert(header: FullNameLastNameFirst, value: String, builder: Person.Builder): Person.Builder = {
      value.tryIt(_.split(header.delimiter).toList)
        .toBuilder(v => {
          fillFirstAndLastName(builder, v match {
            case Nil => PersonalInfo.builder()
            case header :: Nil => PersonalInfo.builder().withLastName(header)
            case header :: remainder => PersonalInfo.builder().withLastName(header).withFirstName(remainder.mkString(" "))
          })
        })(builder)
    }
  }

  implicit object AgeConverter extends HeaderConverter[Age] {
    override def convert(header: Age, value: String, builder: Person.Builder): Person.Builder = {
      value.tryIt(_.toInt).map(age => LocalDateTime.now().minus(age, ChronoUnit.YEARS))
        .map(dt => dt.get(ChronoField.YEAR)).filter(t => t > 1900).toBuilder(v => builder.withPersonalInfo(_.bornYear(v)))(builder)
    }
  }

  implicit object BornYearConverter extends HeaderConverter[BornYear] {
    override def convert(header: BornYear, value: String, builder: Person.Builder): Person.Builder = value.tryIt(_.toInt).toBuilder(v => builder.withPersonalInfo(_.bornYear(v)))(builder)
  }

  implicit object BirthdayConverter extends HeaderConverter[BirthDay] {
    override def convert(header: BirthDay, value: String, builder: Person.Builder): Person.Builder = Option(value).map(_.trim).map(date => header.dateFormat.parse(date))
      .map(dt => dt.get(ChronoField.YEAR)).filter(t => t > 1900).toBuilder(v => builder.withPersonalInfo(_.bornYear(v)))(builder)
  }

  implicit object CityConverter extends HeaderConverter[City] {
    override def convert(header: City, value: String, builder: Person.Builder): Person.Builder = Option(value)
      .toBuilder(v => builder.withAddress(_.city(v)))(builder)
  }

  implicit object StreetConverter extends HeaderConverter[Street] {
    override def convert(header: Street, value: String, builder: Person.Builder): Person.Builder = Option(value)
      .toBuilder(v => builder.withAddress(_.street(v)))(builder)
  }

  implicit object BuildingNoConverter extends HeaderConverter[BuildingNo] {
    override def convert(header: BuildingNo, value: String, builder: Person.Builder): Person.Builder = Option(value)
      .toBuilder(v => builder.withAddress(_.buildingNo(v)))(builder)
  }

  implicit object ApartmentNoConverter extends HeaderConverter[ApartmentNo] {
    override def convert(header: ApartmentNo, value: String, builder: Person.Builder): Person.Builder = Option(value)
      .toBuilder(v => builder.withAddress(_.apartment(v)))(builder)
  }

  implicit object EntranceConverter extends HeaderConverter[Entrance] {
    override def convert(header: Entrance, value: String, builder: Person.Builder): Person.Builder = Option(value)
      .toBuilder(v => builder.withAddress(_.entrance(v)))(builder)
  }

  implicit object NeighborhoodNameConverter extends HeaderConverter[NeighborhoodName] {
    override def convert(header: NeighborhoodName, value: String, builder: Person.Builder): Person.Builder = Option(value)
      .toBuilder(v => builder.withAddress(_.neighborhood(v)))(builder)
  }

  implicit object EmailConverter extends HeaderConverter[Email] {
    override def convert(header: Email, value: String, builder: Person.Builder): Person.Builder = Option(value)
      .toBuilder(v => builder.withEmails(v :: builder.emails))(builder)
  }

  implicit object TagsConverter extends HeaderConverter[Tags] {
    override def convert(header: Tags, value: String, builder: Person.Builder): Person.Builder =
      value.tryIt(_.split(header.delimiter).toList) .toBuilder(builder.withTags)(builder)
  }

  private def parsePhone(value: String, countryToUse: String, localPrefix: String, phoneType: PhoneType): Option[Phone.Builder] = {
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

  implicit object MobilePhoneHeaderConverter extends HeaderConverter[MobilePhoneHeader] {
    override def convert(header: MobilePhoneHeader, value: String, builder: Person.Builder): Person.Builder =
      parsePhone(value, header.countryToUse, header.localPrefix, header.phoneType).toBuilder(v => builder.withPhones(v :: builder.phones))(builder)
  }

  implicit object WorkPhoneHeaderConverter extends HeaderConverter[WorkPhoneHeader] {
    override def convert(header: WorkPhoneHeader, value: String, builder: Person.Builder): Person.Builder =
      parsePhone(value, header.countryToUse, header.localPrefix, header.phoneType).toBuilder(v => builder.withPhones(v :: builder.phones))(builder)
  }

  implicit object HomePhoneHeaderConverter extends HeaderConverter[HomePhoneHeader] {
    override def convert(header: HomePhoneHeader, value: String, builder: Person.Builder): Person.Builder =
      parsePhone(value, header.countryToUse, header.localPrefix, header.phoneType).toBuilder(v => builder.withPhones(v :: builder.phones))(builder)
  }

  implicit class HeaderOp[T](header: T) {
    def toHeader(value: String, builder: Person.Builder)(implicit converter: HeaderConverter[T]): Person.Builder = converter.convert(header, value, builder)
  }

  implicit class StringToOpt(value: String) {
    def tryIt[T](func: String => T):Option[T] = {
      Try(func(value)) match {
        case Success(v) => Option(v)
        case Failure(_) => None
      }
    }
  }

  implicit class OptToBuilder[T](value: Option[T]) {
    def toBuilder[F](func: T => F)(f: F): F = {
      value match {
        case Some(v) => func.apply(v)
        case None => f
      }
    }
  }
}