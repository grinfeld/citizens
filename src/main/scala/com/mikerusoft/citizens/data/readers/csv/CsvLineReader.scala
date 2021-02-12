package com.mikerusoft.citizens.data.readers.csv
import com.mikerusoft.citizens.data.readers.csv.Types.HeaderItem
import com.mikerusoft.citizens.model.{Person, PersonalInfo}
import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec

class CsvLineReader(val headers: HeaderItem, val delimiter: String) extends LineReader with LazyLogging {

  private def normalize(value: String): String = {
    val trimmed = value.trim
    if (trimmed.startsWith("'") && trimmed.endsWith("'"))
      trimmed.substring(1, trimmed.length - 1)
    else
      trimmed
  }

  @tailrec
  private def parseColumns (list: List[(String, Int)], builder: Person.Builder): Person = {

    def fillPhoneBuilder(headerValue: String, header: PhoneNumber): Person.Builder = {
      header.toValue(headerValue, v => builder.withPhones(v :: builder.phones))(builder)
    }

    def fillBothFirstAndLastName(v: PersonalInfo.Builder): Person.Builder = {
      builder.withPersonalInfo(b => b.firstName(v.firstName))
      builder.withPersonalInfo(b => b.lastName(v.lastName))
    }

    list match {
      case Nil => builder.build()
      case head :: remainder =>
        val headerValue = head._1
        headers.get(head._2) match {
          case None => parseColumns(remainder, builder)
          case Some(p) =>
            p match {
              case header: Tz => parseColumns(remainder, header.toValue(headerValue, builder.withTz)(builder))
              case header: Email => parseColumns(remainder, header.toValue(headerValue, v => builder.withEmails(v :: builder.emails))(builder))
              case header: FullNameFirstNameFirst => parseColumns(remainder, header.toValue(headerValue, fillBothFirstAndLastName)(builder))
              case header: FullNameLastNameFirst => parseColumns(remainder, header.toValue(headerValue, fillBothFirstAndLastName)(builder))
              case header: FirstName => parseColumns(remainder, header.toValue(headerValue, v => builder.withPersonalInfo(b => b.firstName(v)))(builder))
              case header: LastName => parseColumns(remainder, header.toValue(headerValue, v => builder.withPersonalInfo(_.lastName(v)))(builder))
              case header: MiddleName => parseColumns(remainder, header.toValue(headerValue, v => builder.withPersonalInfo(_.middleName(v)))(builder))
              case header: Age => parseColumns(remainder, header.toValue(headerValue, v => builder.withPersonalInfo(_.bornYear(v)))(builder))
              case header: BornYear => parseColumns(remainder, header.toValue(headerValue, v => builder.withPersonalInfo(_.bornYear(v)))(builder))
              case header: BirthDay => parseColumns(remainder, header.toValue(headerValue, v => builder.withPersonalInfo(_.bornYear(v)))(builder))
              case header: Remove => parseColumns(remainder, header.toValue(headerValue, builder.withRemove)(builder))
              case header: Tags => parseColumns(remainder, header.toValue(headerValue, builder.withTags)(builder))
              case _: FullAddress => parseColumns(remainder, builder) // todo: fix
              case header: City => parseColumns(remainder, header.toValue(headerValue, v => builder.withAddress(_.city(v)))(builder))
              case header: Street => parseColumns(remainder, header.toValue(headerValue, v => builder.withAddress(_.street(v)))(builder))
              case header: BuildingNo => parseColumns(remainder, header.toValue(headerValue, v => builder.withAddress(_.buildingNo(v)))(builder))
              case header: ApartmentNo => parseColumns(remainder, header.toValue(headerValue, v => builder.withAddress(_.apartment(v)))(builder))
              case header: Entrance => parseColumns(remainder, header.toValue(headerValue, v => builder.withAddress(_.entrance(v)))(builder))
              case header: NeighborhoodName => parseColumns(remainder, header.toValue(headerValue, v => builder.withAddress(_.neighborhood(v)))(builder))
              case header: MobilePhoneHeader => parseColumns(remainder, fillPhoneBuilder(headerValue, header))
              case header: HomePhoneHeader => parseColumns(remainder, fillPhoneBuilder(headerValue, header))
              case header: WorkPhoneHeader => parseColumns(remainder, fillPhoneBuilder(headerValue, header))
            }
      }
    }
  }

  override def readLine(line: String): Person = {
    val data: List[(String, Int)] = line.split(delimiter).map(value => normalize(value)).zipWithIndex
      .filter(pair => headers.contains(pair._2)).toList
    parseColumns(data, Person.builder())
  }
}
