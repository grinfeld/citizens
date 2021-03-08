package com.mikerusoft.citizens.model

import cats.data.Validated
import cats.data.Validated.Valid
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PhoneTest extends AnyFlatSpec with Matchers {
  "when both phone value and type are empty" should "return invalid" in {
    val value1: Validated[String, Phone] = new Phone.Builder(Option.empty, Option.empty).buildWith()
    value1 match {
      case Valid(a) => fail("Should be invalid")
      case Validated.Invalid(e) => assert(e == "Invalid phone value, Invalid phone type")
    }
  }
}
