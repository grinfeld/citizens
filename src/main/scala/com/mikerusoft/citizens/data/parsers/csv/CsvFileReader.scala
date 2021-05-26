package com.mikerusoft.citizens.data.parsers.csv

import com.mikerusoft.citizens.data.parsers.LineParser
import com.mikerusoft.citizens.model.Person
import com.mikerusoft.citizens.model.Types.{HeaderItem, Validation}

case class CsvFileReader(skipHeaders: Boolean, lines: Iterator[String], lineParser: LineParser[Person]) extends Iterator[Validation[Person]] {

  override def hasNext: Boolean = lines.hasNext

  override def next(): Validation[Person] = lineParser.readLine(lines.next())
}

object CsvFileReader {
  def apply(skipHeaders: Boolean, lines: Iterator[String], headers: HeaderItem): CsvFileReader = new CsvFileReader(skipHeaders, lines, CsvLineParser(headers))
}