package com.mikerusoft.citizens.data.readers.csv
import com.mikerusoft.citizens.data.readers.csv.HeaderConverter._
import com.mikerusoft.citizens.data.readers.csv.Types.HeaderItem
import com.mikerusoft.citizens.model.Person
import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec

class CsvLineReader(val headers: HeaderItem, val delimiter: String) extends LineReader with LazyLogging {

  override def readLine(line: String): Person = {
    // todo: split should be replaced by more sophisticated function, since we can have delimiter (for example ',') as part of some text: ('value', 'value1,value2,value3,..,valueN')
    val data: List[(String, Int)] = line.split(delimiter).map(normalize).zipWithIndex
      .filter(pair => headers.contains(pair._2)).toList
    parseColumns(data, Person.builder())
  }

  private def normalize(value: String): String = {
    val trimmed = value.trim
    // todo: to deal with different types of quotes: (',`,")
    if (trimmed.startsWith("'") && trimmed.endsWith("'"))
      trimmed.substring(1, trimmed.length - 1)
    else
      trimmed
  }

  @tailrec
  private def parseColumns (list: List[(String, Int)], builder: Person.Builder): Person = list match {
    case Nil => builder.build()
    case head :: remainder =>
      val headerValue = head._1
      headers.get(head._2) match {
        case None => parseColumns(remainder, builder)
        case Some(p) =>
          // todo: how to replace all this pattern matching with type-classes solution?
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
