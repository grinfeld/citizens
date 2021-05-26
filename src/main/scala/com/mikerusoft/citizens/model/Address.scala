package com.mikerusoft.citizens.model

import cats.data.Validated
import cats.data.Validated.Valid
import cats.implicits.catsSyntaxTuple3Semigroupal
import com.mikerusoft.citizens.model.Person.FilterBlankString
import cats.syntax.option._
import com.mikerusoft.citizens.model.Types.ErrorMsg
import com.mikerusoft.citizens.model.context.Validation._

case class Address(personId: Option[Int], country: String, city: String, street: String, buildingNo: Option[String], apartment: Option[Int], entrance: Option[String], neighborhood: Option[String])
object Address {
  def builder() = new Builder()

  class Builder(var personId: Option[Long], var country: Option[String], var city: Option[String], var street: Option[String], var buildingNo: Option[String], var apartment: Option[Int], var entrance: Option[String], var neighborhood: Option[String], var built: Boolean = false) {
    def this() = this(None, None, None, None, None, None, None, None)

    def country(country: String): Builder = { set(() => this.country = Option(country).filterNotEmpty()) }
    def city(city: String): Builder = { set(() => this.city = Option(city).filterNotEmpty()) }
    def street(street: String): Builder = { set(() => this.street = Option(street).filterNotEmpty()) }
    def buildingNo(buildingNo: String): Builder = { set(() => this.buildingNo = Option(buildingNo).filterNotEmpty()) }
    def apartment(apartment: Int): Builder = { set(() => this.apartment = Option(apartment)) }
    def entrance(entrance: String): Builder = { set(() => this.entrance = Option(entrance).filterNotEmpty()) }
    def neighborhood(neighborhood: String): Builder = { set(() => this.neighborhood = Option(neighborhood).filterNotEmpty()) }
    def personId(personId: Int): Builder = { set(() => this.personId = Option(personId)) }

    private def set(setterFunc: () => Any): Address.Builder = {
      setterFunc.apply()
      built = true;
      this
    }

    def buildWith() : Validated[ErrorMsg, Option[Address]] = {
      if (!built)
        return Valid(Option.empty)
      (country.toValid("Empty Country"), city.toValid("Empty City"), street.toValid("Empty Street"))
        .mapN((country, city, street) => Option(new Address(None, country, city, street, buildingNo, apartment, entrance, neighborhood)))
    }
  }
}

