package com.mikerusoft.citizens.data.readers.csv

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
    CsvLineReader.parseChars(0, line.toCharArray, List()).reverse
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

  private implicit class PairToWord(pair: Word) {
    def index():Int = pair._1
    def word(): String = pair._2
  }

  @tailrec
  private def parsePart(current: Int, chars: Array[Char], acc: String, quoteStarted: Boolean): Word = {
    if (isEndOfString(current, chars))
      return (current, acc)
    val ch = chars(current)
    ch match {
      case ',' => if (quoteStarted) parsePart(current+1, chars, acc+ch, quoteStarted) else (current, acc)
      case ''' =>
        if (quoteStarted) // means closing quotes, let's search for end of cell -> ','
          parsePart(skipBlanks(current+1, chars), chars, acc, quoteStarted = false)
        else
          parsePart(current+1, chars, acc+ch, quoteStarted)
      case _: Char => parsePart(current+1, chars, acc+ch, quoteStarted)
    }
  }

  @tailrec
  private def parseChars(current: Int, chars: Array[Char], acc: List[String]): List[String] = {
    if (isEndOfString(current, chars))
      return acc
    val ch = chars(current)
    val nextNonBlank = if (ch == ',') skipBlanks(current+1, chars) else current
    if (isEndOfString(nextNonBlank, chars))
      return acc
    val nextChar = chars(nextNonBlank)
    val result = nextChar match {
      case ',' => (nextNonBlank+1, "")
      case ''' => parsePart(nextNonBlank+1, chars, "", quoteStarted = true)
      case _: Char => parsePart(nextNonBlank, chars, "", quoteStarted = false)
    }

    if (result.index() >= chars.length)
      result.word().trim() :: acc
    else
      parseChars(result.index(), chars, result.word().trim() :: acc)
  }

  @tailrec
  private def skipBlanks(current: Int, chars: Array[Char]): Int = {
    if (isEndOfString(current, chars))
      return current;
    if (chars(current).toString.isBlank) skipBlanks(current+1, chars) else current
  }

  def isEndOfString(i: Int, chars: Array[Char]): Boolean = if (i >= chars.length) true else false
}
