package com.mikerusoft.citizens

import cats.data.Validated.{Invalid, Valid}
import com.mikerusoft.citizens.data.outputs.DataOutput
import com.mikerusoft.citizens.data.parsers.csv.{CsvFileReader, CsvLineParser, Header}
import com.mikerusoft.citizens.infra.ValidatedWithTryMonad
import com.mikerusoft.citizens.model.Person
import com.mikerusoft.citizens.model.Types.Validation
import com.typesafe.scalalogging.LazyLogging

import scala.io.Source

object Application extends App with LazyLogging {

  val skipHeader = true;
  val output: DataOutput[Person, String] = p => Invalid("")

  ValidatedWithTryMonad.withF((fileName: String) => Source.fromFile(fileName))
    .map(source => source.getLines())
    .fold(lines => CsvFileReader(skipHeader, lines, Map[Int, Header]()))
    .foldList(persons => persons.map(person => output.outputTo(person)))
    .run("myFile.csv")

/*  ValidatedWithTryMonad.withF((num: Int) => num + 1)
    .map(num => List(num, num*2))
    .fold(l => l.map(n => Valid(n)).toIterator)
    .run(1, l => println(l))*/
}

