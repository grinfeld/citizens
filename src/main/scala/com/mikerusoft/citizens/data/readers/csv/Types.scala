package com.mikerusoft.citizens.data.readers.csv

object Types {
  type HeaderItem = Map[Int, Header]

  type Word = (Int, String)

  type Columns = List[String]
}
