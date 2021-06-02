package com.mikerusoft.citizens.model

import cats.data.Validated
import cats.data.Validated.{Valid => catsValid, Invalid => catsInvalid}
import com.mikerusoft.citizens.data.parsers.csv.Header

object Types {
  type HeaderItem = Map[Int, Header]

  type Word = (Int, String)

  type Columns = List[String]

  type ErrorMsg = String

  type Validation[T] = Validated[ErrorMsg, T]

  def Valid[T](t: T): Validation[T] = catsValid(t)

  def Invalid[T](t: String): Validation[T] = catsInvalid(t)
}
