package com.mikerusoft.citizens.data.parsers.csv

import com.mikerusoft.citizens.model.{Person, PersonalInfo, Phone}

import java.time.LocalDateTime
import java.time.temporal.{ChronoField, ChronoUnit}
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

trait HeaderConverter[T] {
  def convert(header: T, value: String)(implicit builder: Person.Builder): Person.Builder
}

object HeaderConverter {

  implicit object HeaderMainConverter extends HeaderConverter[Header] {
    override def convert(header: Header, value: String)(implicit builder: Person.Builder): Person.Builder = {
      header match {
        case header: Tz => header.toHeader(value)
        case header: Email => header.toHeader(value)
        case header: FullNameFirstNameFirst => header.toHeader(value)
        case header: FullNameLastNameFirst => header.toHeader(value)
        case header: FirstName => header.toHeader(value)
        case header: LastName => header.toHeader(value)
        case header: MiddleName => header.toHeader(value)
        case header: Age => header.toHeader(value)
        case header: BornYear => header.toHeader(value)
        case header: BirthDay => header.toHeader(value)
        case header: Remove => header.toHeader(value)
        case header: Tags => header.toHeader(value)
        case header: City => header.toHeader(value)
        case header: Street => header.toHeader(value)
        case header: BuildingNo => header.toHeader(value)
        case header: ApartmentNo => header.toHeader(value)
        case header: Entrance => header.toHeader(value)
        case header: NeighborhoodName => header.toHeader(value)
        case header: MobilePhoneHeader => header.toHeader(value)
        case header: HomePhoneHeader => header.toHeader(value)
        case header: WorkPhoneHeader => header.toHeader(value)
      }
    }
  }

  implicit def optionConverter[A <: Header](implicit ev: HeaderConverter[A]): HeaderConverter[Option[A]] = new HeaderConverter[Option[A]] {
    override def convert(header: Option[A], value: String)(implicit builder: Person.Builder): Person.Builder = header match {
      case Some(h) => h.toHeader(value)
      case None => builder.copy()
    }
  }

  implicit object RemoveConverter extends HeaderConverter[Remove] {
    override def convert(header: Remove, value: String)(implicit builder: Person.Builder): Person.Builder = {
      Option(value).map(_.trim).tryIt(s => if (s.isBlank || s.equals("") || !s.equalsIgnoreCase("true") || s.equals("0")) false else true)
        .toBuilder(builder.withRemove)
    }
  }

  implicit object TzConverter extends HeaderConverter[Tz] {
    @tailrec
    private def appendWithZeros(value: String): String = if (value.length >= 9) value else appendWithZeros("0" + value)

    override def convert(header: Tz, value: String)(implicit builder: Person.Builder): Person.Builder = {
      Option(value).map(_.trim).tryIt(_.toLong.toString).filter(v => v.length <= 9).map(appendWithZeros).toBuilder(builder.withTz)
    }
  }

  implicit object FirstNameConverter extends HeaderConverter[FirstName] {
    override def convert(header: FirstName, value: String)(implicit builder: Person.Builder): Person.Builder =
      Option(value).map(_.trim).toBuilder(v => builder.withPersonalInfoField(b => b.withFirstName(v)))
  }

  implicit object LastNameConverter extends HeaderConverter[LastName] {
    override def convert(header: LastName, value: String)(implicit builder: Person.Builder): Person.Builder =
      Option(value).map(_.trim).toBuilder(v => builder.withPersonalInfoField(b => b.withLastName(v)))
  }

  implicit object MiddleNameConverter extends HeaderConverter[MiddleName] {
    override def convert(header: MiddleName, value: String)(implicit builder: Person.Builder): Person.Builder =
      Option(value).map(_.trim).toBuilder(v => builder.withPersonalInfoField(b => b.withMiddleName(v)))
  }

  private def fillFirstAndLastName(builder: Person.Builder, bs: PersonalInfo.Builder): Person.Builder = {
    builder.withPersonalInfoField(b => b.firstName(bs.firstName)).withPersonalInfoField(b => b.lastName(bs.lastName))
  }

  implicit object FullNameFirstNameFirstConverter extends HeaderConverter[FullNameFirstNameFirst] {
    override def convert(header: FullNameFirstNameFirst, value: String)(implicit builder: Person.Builder): Person.Builder = {
      Option(value).map(_.trim).tryIt(_.split(header.delimiter).toList)
        .toBuilder(names => {
          fillFirstAndLastName(builder, names match {
            case Nil => PersonalInfo.builder()
            case header :: Nil => PersonalInfo.builder().withFirstName(header)
            case header :: remainder => PersonalInfo.builder().withFirstName(header).withLastName(remainder.mkString(" "))
          })
        })
    }
  }

  implicit object FullNameLastNameFirstConverter extends HeaderConverter[FullNameLastNameFirst] {
    override def convert(header: FullNameLastNameFirst, value: String)(implicit builder: Person.Builder): Person.Builder = {
      Option(value).map(_.trim).tryIt(_.split(header.delimiter).toList)
        .toBuilder(names => {
          fillFirstAndLastName(builder, names match {
            case Nil => PersonalInfo.builder()
            case header :: Nil => PersonalInfo.builder().withLastName(header)
            case header :: remainder => PersonalInfo.builder().withLastName(header).withFirstName(remainder.mkString(" "))
          })
        })
    }
  }

  implicit object AgeConverter extends HeaderConverter[Age] {
    override def convert(header: Age, value: String)(implicit builder: Person.Builder): Person.Builder = {
      Option(value).map(_.trim).tryIt(_.toInt).map(age => LocalDateTime.now().minus(age, ChronoUnit.YEARS))
        .map(dt => dt.get(ChronoField.YEAR)).filter(t => t > 1900).toBuilder(v => builder.withPersonalInfoField(_.bornYear(v)))
    }
  }

  implicit object BornYearConverter extends HeaderConverter[BornYear] {
    override def convert(header: BornYear, value: String)(implicit builder: Person.Builder): Person.Builder = Option(value).map(_.trim)
      .tryIt(_.toInt).toBuilder(v => builder.withPersonalInfoField(_.bornYear(v)))
  }

  implicit object BirthdayConverter extends HeaderConverter[BirthDay] {
    override def convert(header: BirthDay, value: String)(implicit builder: Person.Builder): Person.Builder = Option(value).map(_.trim)
      .tryIt(date => header.dateFormat.parse(date)).map(dt => dt.get(ChronoField.YEAR)).filter(t => t > 1900)
      .toBuilder(v => builder.withPersonalInfoField(_.bornYear(v)))
  }

  implicit object CityConverter extends HeaderConverter[City] {
    override def convert(header: City, value: String)(implicit builder: Person.Builder): Person.Builder = Option(value).map(_.trim)
      .toBuilder(v => builder.withAddressField(_.city(v)))
  }

  implicit object StreetConverter extends HeaderConverter[Street] {
    override def convert(header: Street, value: String)(implicit builder: Person.Builder): Person.Builder = Option(value).map(_.trim)
      .toBuilder(v => builder.withAddressField(_.street(v)))
  }

  implicit object BuildingNoConverter extends HeaderConverter[BuildingNo] {
    override def convert(header: BuildingNo, value: String)(implicit builder: Person.Builder): Person.Builder = Option(value).map(_.trim)
      .toBuilder(v => builder.withAddressField(_.buildingNo(v)))
  }

  implicit object ApartmentNoConverter extends HeaderConverter[ApartmentNo] {
    override def convert(header: ApartmentNo, value: String)(implicit builder: Person.Builder): Person.Builder =
      Option(value).map(_.trim).tryIt(_.toInt).toBuilder(v => builder.withAddressField(_.apartment(v)))
  }

  implicit object EntranceConverter extends HeaderConverter[Entrance] {
    override def convert(header: Entrance, value: String)(implicit builder: Person.Builder): Person.Builder = Option(value).map(_.trim)
      .toBuilder(v => builder.withAddressField(_.entrance(v)))
  }

  implicit object NeighborhoodNameConverter extends HeaderConverter[NeighborhoodName] {
    override def convert(header: NeighborhoodName, value: String)(implicit builder: Person.Builder): Person.Builder = Option(value).map(_.trim)
      .toBuilder(v => builder.withAddressField(_.neighborhood(v)))
  }

  implicit object EmailConverter extends HeaderConverter[Email] {
    override def convert(header: Email, value: String)(implicit builder: Person.Builder): Person.Builder = Option(value).map(_.trim)
      .toBuilder(v => builder.withEmails(v :: builder.emails))
  }

  implicit object TagsConverter extends HeaderConverter[Tags] {
    override def convert(header: Tags, value: String)(implicit builder: Person.Builder): Person.Builder =
      Option(value).map(_.trim).tryIt(_.split(header.delimiter).toList) .toBuilder(builder.withTags)
  }

  private def parsePhone(value: String, header: PhoneNumberHeader)(implicit builder: Person.Builder): Person.Builder = {
    (Option(value).map(_.trim).tryIt(_.replaceAll("[\\s\\-\\(\\)]", "").toLong.toString) match {
      case Some(phoneStringValue) =>
        val phoneNum = if (phoneStringValue.startsWith(header.countryToUse)) {
          phoneStringValue
        } else if (phoneStringValue.startsWith(header.localPrefix)){
          header.countryToUse + phoneStringValue.substring(1)
        } else {
          header.countryToUse + phoneStringValue
        }
        Option(Phone.builder().value(phoneNum).`type`(header.phoneType))
      case None => Option(Phone.builder().`type`(header.phoneType))
    }).toBuilder(v => builder.withPhones(v :: builder.phones))
  }

  implicit object MobilePhoneHeaderConverter extends HeaderConverter[MobilePhoneHeader] {
    override def convert(header: MobilePhoneHeader, value: String)(implicit builder: Person.Builder): Person.Builder = parsePhone(value, header)
  }

  implicit object WorkPhoneHeaderConverter extends HeaderConverter[WorkPhoneHeader] {
    override def convert(header: WorkPhoneHeader, value: String)(implicit builder: Person.Builder): Person.Builder = parsePhone(value, header)
  }

  implicit object HomePhoneHeaderConverter extends HeaderConverter[HomePhoneHeader] {
    override def convert(header: HomePhoneHeader, value: String)(implicit builder: Person.Builder): Person.Builder = parsePhone(value, header)
  }

  implicit class HeaderSyntax[T <: Header](header: T) {
    def toHeader(value: String)(implicit builder: Person.Builder, converter: HeaderConverter[T]): Person.Builder = converter.convert(header, value)
  }

  implicit class StringOptWithTry(option: Option[String]) {
    def tryIt[T](func: String => T):Option[T] = {
      option.flatMap(value => Try(func(value)) match {
        case Success(result) => Option(result)
        case Failure(_) => None
      })
    }
  }

  implicit class OptToBuilder[T](option: Option[T]) {
    def toBuilder(func: T => Person.Builder)(implicit f: Person.Builder): Person.Builder = {
      option match {
        case Some(v) => func.apply(v)
        case None => f
      }
    }
  }
}