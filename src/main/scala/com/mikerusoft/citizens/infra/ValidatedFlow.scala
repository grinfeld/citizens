package com.mikerusoft.citizens.infra

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.mikerusoft.citizens.model.Types.{Invalid, Valid, Validation}

import scala.language.higherKinds
import scala.util.{Failure, Success, Try}

case class ValidatedFlow[O](input: Validation[O]) {

  def map[B](func: O => B): ValidatedFlow[B] = {
    new ValidatedFlow(input match {
      case Valid(i) => Try(func(i)) match {
        case Success(value) => Valid(value)
        case Failure(e) => Invalid(e.getMessage)
      }
      case Invalid(m) => Invalid(m)
    })
  }

  def convert[B](func: O => B): ValidatedFlow[B] = map(func)

  def fold[B](func: O => Iterator[Validation[B]]): ValidatedFlow[List[B]] = {
    foldM(List[B]())((ls, p:B) => p :: ls)(func)
  }

  def foldM[B, L](z: L)(agg: (L, B) => L)(func: O => Iterator[Validation[B]]): ValidatedFlow[L] = {
    new ValidatedFlow[L](input match {
      case Valid(i) =>
        func(i).foldLeft(Valid(z))((acc, ph) => (acc, ph).mapN((ls, p) => agg.apply(ls, p)))
      case Invalid(m) => Invalid(m)
    })
  }

  def foldList[B](func: O => List[Validation[B]]): ValidatedFlow[List[B]] = {
    fold((in: O) => func(in).toIterator)
  }

  def flatMap[B](func: O => Validation[B]): ValidatedFlow[B] = {
    new ValidatedFlow[B](input match {
      case Valid(i) => func(i)
      case Invalid(m) => Invalid(m)
    })
  }

  def run(): Validation[O] = input
}

object ValidatedFlow {
  def apply[A](data: => A): ValidatedFlow[A] = {
    new ValidatedFlow(Try(data) match {
      case Success(value) => Valid(value)
      case Failure(e) => Invalid(e.getMessage)
    })
  }
}

