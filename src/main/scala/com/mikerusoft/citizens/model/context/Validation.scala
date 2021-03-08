package com.mikerusoft.citizens.model.context

import cats.kernel.Semigroup

object Validation {
  implicit val stringSemigroup: Semigroup[String] = Semigroup.instance[String](_ + ", " + _)
}
