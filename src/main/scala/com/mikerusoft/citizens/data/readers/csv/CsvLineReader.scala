package com.mikerusoft.citizens.data.readers.csv

import com.mikerusoft.citizens.data.readers.csv.CsvLineReader.PairToWord
import com.mikerusoft.citizens.data.readers.csv.HeaderConverter._
import com.mikerusoft.citizens.data.readers.csv.Types.{HeaderItem, Word}
import com.mikerusoft.citizens.model.Person
import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec

class CsvLineReader(val headers: HeaderItem, val delimiter: String) extends LineReader with LazyLogging {

  override def readLine(line: String): Person = {
    val data: List[(String, Int)] = parseLine(line).zipWithIndex
      .filter(pair => headers.contains(pair._2))
    parseColumns(data, Person.builder())
  }

  def parseLine(line: String): List[String] = {

    @tailrec
    def parsePart(current: Int, chars: Array[Char], acc: String, quoteStarted: Boolean): Word = {
      if (current >= chars.length)
        return (current, acc)
      val ch = chars(current)
      ch match {
        case ',' => if (quoteStarted) parsePart(current+1, chars, acc+ch, quoteStarted) else (current+1, acc)
        case '\'' =>
          if (quoteStarted && current+1 >= chars.length)
            (current+1, acc)
          else if (quoteStarted && chars(current + 1) == ',')
            (current+3, acc)
          else
            parsePart(current+1, chars, acc+ch, quoteStarted)
        case _: Char => parsePart(current+1, chars, acc+ch, quoteStarted)
      }
    }

    @tailrec
    def parseChars(current: Int, chars: Array[Char], acc: List[String]): List[String] = {
      val ch = chars(current)
      val result = ch match {
        case '\'' => parsePart(current+1, chars, "", quoteStarted = true)
        case _: Char => parsePart(current+1, chars, s"$ch", quoteStarted = false)
      }

      if (result.index() >= chars.length)
        result.word().trim() :: acc
      else
        parseChars(result.index(), chars, result.word().trim() :: acc)
    }

    parseChars(0, line.toCharArray, List()).reverse
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

object CsvLineReader {
  implicit class PairToWord(pair: Word) {
    def index():Int = pair._1
    def word(): String = pair._2
  }
}
