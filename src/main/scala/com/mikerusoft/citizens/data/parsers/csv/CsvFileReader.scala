package com.mikerusoft.citizens.data.parsers.csv

import com.mikerusoft.citizens.data.parsers.LineParser
import com.mikerusoft.citizens.model.Types._

class CsvFileReader[T] (val skipHeaders: Boolean, val lines: Iterator[String], val lineParser: LineParser[T]){
  def mapLine[B](func: T => Validation[B]): Iterator[Validation[B]] = lines.zipWithIndex.map(pair => lineParser.readLine(pair._1, pair._2) match {
    case Valid(a) => func(a)
    case Invalid(e) => Invalid(e)
  })
}

object CsvFileReader {
  def apply[T](skipHeaders: Boolean, lines: Iterator[String])(implicit lineParser: LineParser[T]): CsvFileReader[T] = new CsvFileReader(skipHeaders, lines, lineParser)
  def parseLines[T](skipHeaders: Boolean, lines: Iterator[String])(implicit lineParser: LineParser[T]): CsvFileReader[T] = new CsvFileReader(skipHeaders, lines, lineParser)
}