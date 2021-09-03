package com.mikerusoft.citizens.model

import cats.data.Validated
import cats.implicits.catsSyntaxTuple3Semigroupal
import com.mikerusoft.citizens.model.Person.FilterBlankString
import cats.syntax.option._
import com.mikerusoft.citizens.model.Types.{ErrorMsg, Valid}
import com.mikerusoft.citizens.model.context.Validation._

case class Address(personId: Option[Int], country: String, city: String, street: String, buildingNo: Option[String], apartment: Option[Int], entrance: Option[String], neighborhood: Option[String])
object Address {
  def builder(): Builder = Builder(None, None, None, None, None, None, None, None)

  case class Builder private[Address] (var personId: Option[Long], var country: Option[String], var city: Option[String], var street: Option[String], var buildingNo: Option[String], var apartment: Option[Int], var entrance: Option[String], var neighborhood: Option[String]) {

    def country(country: String): Builder = { copy(country = Option(country).filterNotEmpty()) }
    def city(city: String): Builder = { copy(city = Option(city).filterNotEmpty()) }
    def street(street: String): Builder = { copy(street = Option(street).filterNotEmpty()) }
    def buildingNo(buildingNo: String): Builder = { copy(buildingNo = Option(buildingNo).filterNotEmpty()) }
    def apartment(apartment: Int): Builder = { copy(apartment = Option(apartment)) }
    def entrance(entrance: String): Builder = { copy(entrance = Option(entrance).filterNotEmpty()) }
    def neighborhood(neighborhood: String): Builder = { copy(neighborhood = Option(neighborhood).filterNotEmpty()) }
    def personId(personId: Int): Builder = { copy(personId = Option(personId)) }

    def buildWith() : Validated[ErrorMsg, Option[Address]] = {
      this match {
        case Builder(None, None, None, None, None, None, None, None) => Valid(Option.empty)
        case _ => (country.toValid("Empty Country"), city.toValid("Empty City"), street.toValid("Empty Street"))
          .mapN((country, city, street) => Option(new Address(None, country, city, street, buildingNo, apartment, entrance, neighborhood)))
      }
    }
  }
}

