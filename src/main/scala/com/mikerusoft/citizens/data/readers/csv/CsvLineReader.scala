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
  private def rec (list: List[(String, Int)], builder: Person.Builder): Person = {
    list match {
      case Nil => builder.build()
      case head :: remainder =>
        headers.get(head._2) match {
          case None => rec(remainder, builder)
          case Some(p) =>
            p match {
              case value: Tz => rec(remainder, value.toValue(head._1, builder.withTz)(builder))
              case value: Email => rec(remainder, value.toValue(head._1, v => builder.withEmails(v :: builder.emails))(builder))
              case _: FullName => rec(remainder, builder) // todo: fix
              case value: FirstName => rec(remainder, value.toValue(head._1, v => builder.withPersonalInfo(builder.personalInfo.firstName(v)))(builder))
              case value: LastName => rec(remainder, value.toValue(head._1, v => builder.withPersonalInfo(builder.personalInfo.lastName(v)))(builder))
              case value: MiddleName => rec(remainder, value.toValue(head._1, v => builder.withPersonalInfo(builder.personalInfo.middleName(v)))(builder))
              case value: Age => rec(remainder, value.toValue(head._1, v => builder.withPersonalInfo(builder.personalInfo.bornYear(v)))(builder))
              case value: BornYear => rec(remainder, value.toValue(head._1, v => builder.withPersonalInfo(builder.personalInfo.bornYear(v)))(builder))
              case value: BirthDay => rec(remainder, value.toValue(head._1, v => builder.withPersonalInfo(builder.personalInfo.bornYear(v)))(builder))
              case value: Remove => rec(remainder, value.toValue(head._1, builder.withRemove)(builder))
              case value: Tags => rec(remainder, value.toValue(head._1, builder.withTags)(builder))
              case _: FullAddress => rec(remainder, builder) // todo: fix
              case value: City => rec(remainder, value.toValue(head._1, v => builder.withAddress(builder.address.city(v)))(builder))
              case value: Street => rec(remainder, value.toValue(head._1, v => builder.withAddress(builder.address.street(v)))(builder))
              case value: BuildingNo => rec(remainder, value.toValue(head._1, v => builder.withAddress(builder.address.buildingNo(v)))(builder))
              case value: ApartmentNo => rec(remainder, value.toValue(head._1, v => builder.withAddress(builder.address.apartment(v)))(builder))
              case value: Entrance => rec(remainder, value.toValue(head._1, v => builder.withAddress(builder.address.entrance(v)))(builder))
              case value: NeighborhoodName => rec(remainder, value.toValue(head._1, v => builder.withAddress(builder.address.neighborhood(v)))(builder))
              case value: MobilePhone => rec(remainder, value.toValue(head._1, v => builder.withPhones(v.toBuilder() :: builder.phones))(builder))
              case value: HomePhone => rec(remainder, value.toValue(head._1, v => builder.withPhones(v.toBuilder() :: builder.phones))(builder))
              case value: WorkPhone => rec(remainder, value.toValue(head._1, v => builder.withPhones(v.toBuilder() :: builder.phones))(builder))
            }
        }
    }
  }

  override def readLine(line: String): Person = {
    val data: List[(String, Int)] = line.split(delimiter).map(value => normalize(value)).zipWithIndex
      .filter(pair => headers.contains(pair._2)).toList
    rec(data, Person.builder())
  }
}
