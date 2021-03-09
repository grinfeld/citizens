package com.mikerusoft.citizens.model

import com.mikerusoft.citizens.data.parsers.csv.Header

object Types {
  type HeaderItem = Map[Int, Header]

  type Word = (Int, String)

  type Columns = List[String]

  type ErrorMsg = String
}
