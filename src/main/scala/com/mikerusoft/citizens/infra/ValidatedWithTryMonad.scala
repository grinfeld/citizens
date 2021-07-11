package com.mikerusoft.citizens.infra

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.mikerusoft.citizens.model.Types.{Invalid, Valid, Validation}

import scala.language.higherKinds
import scala.util.{Failure, Success, Try}

case class ValidatedWithTryMonad[I, O](action: I => Validation[O]) {

  def map[B](func: O => B): ValidatedWithTryMonad[I, B] = {
    new ValidatedWithTryMonad((input: I) => action(input) match {
      case Valid(i) => Try(func(i)) match {
        case Success(value) => Valid(value)
        case Failure(e) => Invalid(e.getMessage)
      }
      case Invalid(m) => Invalid(m)
    })
  }

  def convert[B](func: O => B): ValidatedWithTryMonad[I, B] = map(func)

  def fold[B](func: O => Iterator[Validation[B]]): ValidatedWithTryMonad[I, List[B]] = {
    foldM(List[B]())((ls, p:B) => p :: ls)(func)
  }

  def foldM[B, L](z: L)(agg: (L, B) => L)(func: O => Iterator[Validation[B]]): ValidatedWithTryMonad[I, L] = {
    new ValidatedWithTryMonad[I, L]((input: I) => action(input) match {
      case Valid(i) =>
        func(i).foldLeft(Valid(z))((acc, ph) => (acc, ph).mapN((ls, p) => agg.apply(ls, p)))
      case Invalid(m) => Invalid(m)
    })
  }

  def foldList[B](func: O => List[Validation[B]]): ValidatedWithTryMonad[I, List[B]] = {
    fold((in: O) => func(in).toIterator)
  }

  def flatMap[B](func: O => Validation[B]): ValidatedWithTryMonad[I, B] = {
    new ValidatedWithTryMonad[I, B]((input: I) => action(input) match {
      case Valid(i) => func(i)
      case Invalid(m) => Invalid(m)
    })
  }

  def run(input: I): Validation[O] = action(input)
}

object ValidatedWithTryMonad {
  def startFromAction[A, B](func: A => B): ValidatedWithTryMonad[A, B] =
    ValidatedWithTryMonad(
      (input: A) => Try(func(input)) match {
        case Success(value) => Valid(value)
        case Failure(e) => Invalid(e.getMessage)
      }
    )
}

