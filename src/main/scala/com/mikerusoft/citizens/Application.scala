package com.mikerusoft.citizens

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.mikerusoft.citizens.data.outputs.DataOutput
import com.mikerusoft.citizens.data.parsers.csv.{CsvFileReader, CsvLineParser}
import com.mikerusoft.citizens.infra.ValidatedWithTryMonad
import com.mikerusoft.citizens.model.Person
import com.mikerusoft.citizens.model.Types.{Invalid, Validation, Valid}
import com.typesafe.scalalogging.LazyLogging
import com.mikerusoft.citizens.model.context.Validation._
import cats.data.Validated.{Valid => catsValid, Invalid => catsInvalid}

import scala.io.Source
import scala.util.{Failure, Success, Try}

object Application extends App with LazyLogging {

  val skipHeader = true
  val input = new CsvLineParser(Map())
  val output: DataOutput[Person, String] = p => Invalid("")
  val fileName: String = "myFile.csv"
  val maxErrorsToStop: Int = 100;

  // first bad version
  // num of valid records + accumulated error string
  val result1: Validation[Int] = Try(Source.fromFile(fileName)) match {
    case Failure(exception) => Invalid(exception.getMessage)
    case Success(value) =>
      value.getLines().map(line =>
        input.readLine(line)).map {
          case catsValid(person) => output.outputTo(person)
          case catsInvalid(e) => Invalid(e)
        }.map {
          case catsValid(_) => Valid(1)
          case catsInvalid(e) => Invalid(e)
        }.fold(Valid(0))((acc, vl) => (acc, vl).mapN( (first, second) => first + second))
  }

  // second bad version
  // num of valid records + accumulated error string
  val result2 =
    ValidatedWithTryMonad.withF((fileName: String) => Source.fromFile(fileName))
    .map(source => source.getLines())
    .map(lines => CsvFileReader(skipHeader, lines)(input))
    .map(fileReader => {
        fileReader.mapLine(p => output.outputTo(p))
        .map {
          case catsValid(_) => Valid(1)
          case catsInvalid(e) => Invalid(e)
        }.toList
    })
    .map(ls => ls.fold(Valid(0))( (acc, vl) => (acc, vl).mapN( (first, second) => first + second) ))
    .run(fileName)

}

