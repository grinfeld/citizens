package com.mikerusoft.citizens.data.parsers.csv

import cats.data.Validated.{Invalid, Valid}
import com.mikerusoft.citizens.data.parsers.LineParser
import com.mikerusoft.citizens.model.Types.{HeaderItem, Validation}

case class CsvFileReader[T] private (skipHeaders: Boolean, lines: Iterator[String], lineParser: LineParser[T]){
  def mapLine[B](func: T => Validation[B]): Iterator[Validation[B]] = lines.map(line => lineParser.readLine(line) match {
    case Valid(a) => func(a)
    case Invalid(e) => Invalid(e)
  })
}

object CsvFileReader {
  def apply[T](skipHeaders: Boolean, lines: Iterator[String])(implicit lineParser: LineParser[T]): CsvFileReader[T] = new CsvFileReader(skipHeaders, lines, lineParser)
}