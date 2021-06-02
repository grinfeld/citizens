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

  object Valid {
    def apply[T](t: T): Validation[T] = catsValid(t)
    def unapply[T](v: Validation[T]): Option[T] = v match {
      case catsValid(v) => Some(v)
      case catsInvalid(_) => None
    }
  }

  object Invalid {
    def apply[T](error: ErrorMsg): Validation[T] = catsInvalid(error)
    def unapply[T](v: Validation[T]): Option[ErrorMsg] = v match {
      case catsValid(v) => None
      case catsInvalid(e) => Some(e)
    }
  }
}
