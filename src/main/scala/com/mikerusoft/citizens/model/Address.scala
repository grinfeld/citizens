package com.mikerusoft.citizens.model

import cats.data.Validated
import cats.data.Validated.Valid
import cats.implicits.catsSyntaxTuple3Semigroupal
import com.mikerusoft.citizens.model.Person.FilterBlankString
import cats.syntax.option._
import com.mikerusoft.citizens.model.context.Validation._

case class Address(country: String, city: String, street: String, buildingNo: Option[String], apartment: Option[String],
                                        entrance: Option[String], neighborhood: Option[String])
object Address {
  def builder() = new Builder()

  class Builder(var country: Option[String], var city: Option[String], var street: Option[String], var buildingNo: Option[String],
                       var apartment: Option[String], var entrance: Option[String], var neighborhood: Option[String], var built: Boolean = false) {
    def this() = this(None, None, None, None, None, None, None)

    def country(country: String): Builder = { set(() => this.country = Option(country).filterNotEmpty()) }
    def city(city: String): Builder = { set(() => this.city = Option(city).filterNotEmpty()) }
    def street(street: String): Builder = { set(() => this.street = Option(street).filterNotEmpty()) }
    def buildingNo(buildingNo: String): Builder = { set(() => this.buildingNo = Option(buildingNo).filterNotEmpty()) }
    def apartment(apartment: String): Builder = { set(() => this.apartment = Option(apartment).filterNotEmpty()) }
    def entrance(entrance: String): Builder = { set(() => this.entrance = Option(entrance).filterNotEmpty()) }
    def neighborhood(neighborhood: String): Builder = { set(() => this.neighborhood = Option(neighborhood).filterNotEmpty()) }

    private def set(f: () => Any): Address.Builder = {
      f.apply()
      built = true;
      this
    }

    def build(): Option[Address] = if (built) Option(Address(country.get, city.get, street.get, buildingNo, apartment, entrance, neighborhood)) else None

    def buildWith() : Validated[String, Option[Address]] = {
      if (!built)
        return Valid(Option.empty)
      (country.toValid("Empty Country"), city.toValid("Empty City"), street.toValid("Empty Street"))
        .mapN((country, city, street) => Option(new Address(country, city, street, buildingNo, apartment, entrance, neighborhood)))
    }
  }
}

