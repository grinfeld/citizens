package com.mikerusoft.citizens.data.readers.csv
import com.mikerusoft.citizens.data.readers.csv.HeaderConverter._
import com.mikerusoft.citizens.data.readers.csv.Types.HeaderItem
import com.mikerusoft.citizens.model.Person
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

    list match {
      case Nil => builder.build()
      case head :: remainder =>
        val headerValue = head._1
        headers.get(head._2) match {
          case None => parseColumns(remainder, builder)
          case Some(p) =>
            //parseColumns(remainder, p.toHeader(headerValue, builder))
            p match {
              case header: Tz => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: Email => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: FullNameFirstNameFirst => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: FullNameLastNameFirst => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: FirstName => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: LastName => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: MiddleName => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: Age => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: BornYear => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: BirthDay => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: Remove => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: Tags => parseColumns(remainder, header.toHeader(headerValue, builder))
              // case _: FullAddress => parseColumns(remainder, builder) // todo: fix
              case header: City => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: Street => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: BuildingNo => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: ApartmentNo => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: Entrance => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: NeighborhoodName => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: MobilePhoneHeader => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: HomePhoneHeader => parseColumns(remainder, header.toHeader(headerValue, builder))
              case header: WorkPhoneHeader => parseColumns(remainder, header.toHeader(headerValue, builder))
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
