package com.mikerusoft.citizens.data.readers.csv

import com.mikerusoft.citizens.model.Person

trait LineReader {
  def readLine(line: String): Person
}
