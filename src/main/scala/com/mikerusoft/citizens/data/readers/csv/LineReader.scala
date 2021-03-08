package com.mikerusoft.citizens.data.readers.csv

import cats.data.Validated
import com.mikerusoft.citizens.model.Person

trait LineReader {
  def readLine(line: String): Validated[String, Person]
}
