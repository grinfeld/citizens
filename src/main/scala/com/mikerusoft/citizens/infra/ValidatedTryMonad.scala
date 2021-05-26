package com.mikerusoft.citizens.infra

import cats.data.Validated.{Invalid, Valid}
import cats.implicits.catsSyntaxTuple2Semigroupal
import com.mikerusoft.citizens.model.Types.Validation

import scala.language.higherKinds
import scala.util.{Failure, Success, Try}

case class ValidatedTryMonad[A](get: Validation[A]) {

  def this(tryIt: Try[A]) = this(tryIt match {
    case Success(value) => Valid(value)
    case Failure(e) => Invalid(e.getMessage)
  })

  def map[B](func: A => B): ValidatedTryMonad[B] = {
    ValidatedTryMonad[B](get match {
      case Valid(value) => Try(func(value)) match {
        case Success(v) => Valid(v)
        case Failure(e) => Invalid(e.getMessage)
      }
      case Invalid(e) => Invalid(e)
    })
  }

  def fold[B](func: A => Iterator[Validation[B]]): ValidatedTryMonad[List[B]] = {
    ValidatedTryMonad[List[B]](get match {
      case Valid(value) => func(value).foldLeft(Valid(List[B]()).asInstanceOf[Validation[List[B]]])((acc, ph) => (acc, ph).mapN((ls, p) => p :: ls))
      case Invalid(e) => Invalid(e)
    })
  }

  def fold[B](func: A => List[Validation[B]]): ValidatedTryMonad[List[B]] = {
    fold(list => func(list).iterator)
  }

  def flatMap[B](func: A => ValidatedTryMonad[B]): ValidatedTryMonad[B] = get match {
    case Valid(v) => func(v)
    case Invalid(e) => ValidatedTryMonad[B](Invalid(e))
  }
}

object ValidatedTryMonad {
  def apply[A](tryIt: Try[A]) = new ValidatedTryMonad[A](tryIt)
}

