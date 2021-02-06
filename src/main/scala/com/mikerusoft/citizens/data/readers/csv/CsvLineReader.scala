package com.mikerusoft.citizens.data.readers.csv
import com.mikerusoft.citizens.data.readers.csv.Types.HeaderItem
import com.mikerusoft.citizens.model.Person

import scala.annotation.tailrec

class CsvLineReader(val headers: HeaderItem, val delimiter: String) extends LineReader {

  private def normalize(value: String): String = {
    val trimmed = value.trim
    if (trimmed.startsWith("'") && trimmed.endsWith("'"))
      trimmed.substring(1, trimmed.length - 1)
    else
      trimmed
  }

  @tailrec
  private def parseColumns (list: List[(String, Int)], builder: Person.Builder): Person = {
    list match {
      case Nil => builder.build()
      case head :: remainder =>
        val headerValue = head._1
        val header = head._2
        headers.get(header) match {
          case None => parseColumns(remainder, builder)
          case Some(p) =>
            p match {
              case value: Tz => parseColumns(remainder, value.toValue(headerValue, builder.withTz)(builder))
              case value: Email => parseColumns(remainder, value.toValue(headerValue, v => builder.withEmails(v :: builder.emails))(builder))
              case _: FullName => parseColumns(remainder, builder) // todo: fix
              case value: FirstName => parseColumns(remainder, value.toValue(headerValue, v => builder.withPersonalInfo(builder.personalInfo.firstName(v)))(builder))
              case value: LastName => parseColumns(remainder, value.toValue(headerValue, v => builder.withPersonalInfo(builder.personalInfo.lastName(v)))(builder))
              case value: MiddleName => parseColumns(remainder, value.toValue(headerValue, v => builder.withPersonalInfo(builder.personalInfo.middleName(v)))(builder))
              case value: Age => parseColumns(remainder, value.toValue(headerValue, v => builder.withPersonalInfo(builder.personalInfo.bornYear(v)))(builder))
              case value: BornYear => parseColumns(remainder, value.toValue(headerValue, v => builder.withPersonalInfo(builder.personalInfo.bornYear(v)))(builder))
              case value: BirthDay => parseColumns(remainder, value.toValue(headerValue, v => builder.withPersonalInfo(builder.personalInfo.bornYear(v)))(builder))
              case value: Remove => parseColumns(remainder, value.toValue(headerValue, builder.withRemove)(builder))
              case value: Tags => parseColumns(remainder, value.toValue(headerValue, builder.withTags)(builder))
              case _: FullAddress => parseColumns(remainder, builder) // todo: fix
              case value: City => parseColumns(remainder, value.toValue(headerValue, v => builder.withAddress(builder.address.city(v)))(builder))
              case value: Street => parseColumns(remainder, value.toValue(headerValue, v => builder.withAddress(builder.address.street(v)))(builder))
              case value: BuildingNo => parseColumns(remainder, value.toValue(headerValue, v => builder.withAddress(builder.address.buildingNo(v)))(builder))
              case value: ApartmentNo => parseColumns(remainder, value.toValue(headerValue, v => builder.withAddress(builder.address.apartment(v)))(builder))
              case value: Entrance => parseColumns(remainder, value.toValue(headerValue, v => builder.withAddress(builder.address.entrance(v)))(builder))
              case value: NeighborhoodName => parseColumns(remainder, value.toValue(headerValue, v => builder.withAddress(builder.address.neighborhood(v)))(builder))
              case value: MobilePhone => parseColumns(remainder, value.toValue(headerValue, v => builder.withPhones(v.toBuilder() :: builder.phones))(builder))
              case value: HomePhone => parseColumns(remainder, value.toValue(headerValue, v => builder.withPhones(v.toBuilder() :: builder.phones))(builder))
              case value: WorkPhone => parseColumns(remainder, value.toValue(headerValue, v => builder.withPhones(v.toBuilder() :: builder.phones))(builder))
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
