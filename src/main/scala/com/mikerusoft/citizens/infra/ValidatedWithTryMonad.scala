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

  def fold[B](func: O => Iterator[Validation[B]]): ValidatedWithTryMonad[I, List[B]] = {
    new ValidatedWithTryMonad[I, List[B]]((input: I) => action(input) match {
      case Valid(i) =>
        func(i).foldLeft(Valid(List[B]()))((acc, ph) => (acc, ph).mapN((ls, p) => p :: ls))
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

/*  def run(input: I)(onSuccess: O => Unit): Validation[O] = {
    val result = action(input)
    result match {
      case Valid(r) => onSuccess(r)
    }
    result
  }*/
}

object ValidatedWithTryMonad {
  def withF[A, B](func: A => B): ValidatedWithTryMonad[A, B] =
    ValidatedWithTryMonad(
      (input: A) => Try(func(input)) match {
        case Success(value) => Valid(value)
        case Failure(e) => Invalid(e.getMessage)
      }
    )
}

