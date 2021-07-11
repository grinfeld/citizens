package com.mikerusoft.citizens

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.mikerusoft.citizens.data.outputs.DataOutput
import com.mikerusoft.citizens.data.parsers.csv.{CsvFileReader, CsvLineParser}
import com.mikerusoft.citizens.infra.ValidatedWithTryMonad
import com.mikerusoft.citizens.model.Person
import com.mikerusoft.citizens.model.Types.{Invalid, Valid, Validation}
import com.typesafe.scalalogging.LazyLogging
import com.mikerusoft.citizens.model.context.Validation._

import scala.io.Source
import scala.util.{Failure, Success, Try}

object Application extends App with LazyLogging {

  val skipHeader = true
  val input = new CsvLineParser(Map())
  val output: DataOutput[Person, String] = p => Invalid("")
  val fileName: String = "myFile.csv"
  val maxErrorsToStop: Int = 100;

  def option1(): Validation[Int] = {
    // first bad version
    // num of valid records + accumulated error string
    val result1: Validation[Int] =
      Try(Source.fromFile(fileName)) match {
        case Failure(exception) => Invalid(exception.getMessage)
        case Success(fileReader) =>
          fileReader.getLines().map(line =>
            input.readLine(line)).map {
            case Valid(person) => output.outputTo(person)
            case Invalid(e) => Invalid(e)
          }.map {
            case Valid(_) => Valid(1)
            case Invalid(e) => Invalid(e)
          }
          .fold(Valid(0))((acc, vl) => (acc, vl).mapN( (first, second) => first + second))

      }
    result1
  }

  def option2(): Validation[Int] = {
    // second bad version
    // num of valid records + accumulated error string
    val result2: Validation[Int] =
      ValidatedWithTryMonad.startFromAction((fileName:String) => Source.fromFile(fileName))
        .convert(source => source.getLines())
        .convert(lines => CsvFileReader.parseLines(skipHeader, lines)(input))
        .convert(parsedPersons => parsedPersons.mapLine(p => output.outputTo(p)))
        .convert(validatedPersons => validatedPersons.map {
          case Valid(_) => Valid(1)
          case Invalid(e) => Invalid(e).asInstanceOf[Validation[Int]]
        })
        .foldM(0)((first, second:Int) => first + second)(it => it)
        .run(fileName)
    result2
  }
}

