package com.mikerusoft.citizens

import cats.data.Validated.{Invalid, Valid}
import com.mikerusoft.citizens.data.outputs.DataOutput
import com.mikerusoft.citizens.data.parsers.csv.CsvLineParser
import com.mikerusoft.citizens.model.Person
import com.typesafe.scalalogging.LazyLogging

import scala.io.{BufferedSource, Source}

object Application extends App with LazyLogging {

  val skipHeader = true;

  private val source: BufferedSource = Source.fromFile("stam.csv")
  private val lines: Iterator[String] = source.getLines()

  private val lineParser = CsvLineParser(Map())
  val output: DataOutput[Person, Any] = ???

  if (skipHeader)
    lines.next()

  for (line <- lines) {
    lineParser.readLine(line).andThen(person => output.outputTo(person)) match {
      case Valid(_) => // happy...
      case Invalid(e) => logger.warn(s"Failed to read line with: $e") // do something with errors
    }
  }

  source.close()
}
