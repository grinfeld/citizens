package com.mikerusoft.citizens.data.parsers.csv

import com.mikerusoft.citizens.data.parsers.LineParser
import com.mikerusoft.citizens.data.parsers.csv.HeaderConverter._
import com.mikerusoft.citizens.model.Types.{Columns, HeaderItem, Validation, Word}
import com.mikerusoft.citizens.model.Person
import com.mikerusoft.citizens.data.parsers.csv.CsvLineParser._
import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec

 case class CsvLineParser(headers: HeaderItem) extends LineParser[Person] with LazyLogging {

  override def readLine(line: String, lineNum: Int): Validation[Person] = {
    val data: List[(String, Int)] = CsvLineParser.parseCsvLine(line).zipWithIndex
      .filter(pair => headers.contains(pair._2))
    parseColumns(data)(Person.builder(lineNum))
  }

  @tailrec
  private def parseColumns (list: List[(String, Int)])(implicit builder: Person.Builder): Validation[Person] = list match {
    case Nil => builder.buildWith()
    case head :: remainder =>
      val headerValue = head.value
      headers.get(head.index) match {
        case None => parseColumns(remainder)
        case Some(header) => parseColumns(remainder)(header.toHeader(headerValue))
      }
  }
}

object CsvLineParser {
  implicit class HeaderItemWrapper(t2: (String, Int)) {
    def value: String = t2._1
    def index: Int = t2._2
  }

  def apply(headers: HeaderItem): LineParser[Person] = {
    new CsvLineParser(headers)
  }

  def parseCsvLine(line: String) : Columns = {
    parseChars(0, line.toCharArray, List()).reverse
  }

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
  private def parseChars(current: Int, chars: Array[Char], acc: Columns): Columns = {
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
      current
    else if (chars(current).toString.isBlank)
      skipBlanks(current+1, chars)
    else
      current
  }

  private def isEndOfString(i: Int, chars: Array[Char]): Boolean = if (i >= chars.length) true else false
}
