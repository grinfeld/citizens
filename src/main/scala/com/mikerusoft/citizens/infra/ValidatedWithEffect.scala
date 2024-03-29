package com.mikerusoft.citizens.infra

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.mikerusoft.citizens.model.Types.{Invalid, Valid, Validation}

import scala.language.higherKinds
import scala.util.{Failure, Success, Try}

case class ValidatedWithEffect[O](input: Validation[O]) {

  def map[B](func: O => B): ValidatedWithEffect[B] = {
    new ValidatedWithEffect(input match {
      case Valid(i) => Try(func(i)) match {
        case Success(value) => Valid(value)
        case Failure(e) => Invalid(e.getMessage)
      }
      case Invalid(m) => Invalid(m)
    })
  }

  def convert[B](func: O => B): ValidatedWithEffect[B] = map(func)

  def fold[B](func: O => Iterator[Validation[B]]): ValidatedWithEffect[List[B]] = {
    foldM(List[B]())((ls, p:B) => p :: ls)(func)
  }

  def foldM[B, L](z: L)(agg: (L, B) => L)(func: O => Iterator[Validation[B]]): ValidatedWithEffect[L] = {
    new ValidatedWithEffect[L](input match {
      case Valid(i) =>
        func(i).foldLeft(Valid(z))((acc, ph) => (acc, ph).mapN((ls, p) => agg.apply(ls, p)))
      case Invalid(m) =>
        Invalid[L](m).toValidatedNel[String, L].leftMap(l => l.foldLeft("")((all: String, error: String) => all + ", " + error)) match {
          case Valid(e) => Valid(e)
          case Invalid(e) => Invalid(e.stripTrailing())
        }
    })
  }

  def foldList[B](func: O => List[Validation[B]]): ValidatedWithEffect[List[B]] = {
    fold((in: O) => func(in).toIterator)
  }

  def flatMap[B](func: O => Validation[B]): ValidatedWithEffect[B] = {
    new ValidatedWithEffect[B](input match {
      case Valid(i) => func(i)
      case Invalid(m) => Invalid(m)
    })
  }

  def run: Validation[O] = input
}

object ValidatedWithEffect {
  def apply[A](data: => A): ValidatedWithEffect[A] = {
    new ValidatedWithEffect(Try(data) match {
      case Success(value) => Valid(value)
      case Failure(e) => Invalid(e.getMessage)
    })
  }
}

