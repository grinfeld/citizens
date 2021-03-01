package com.mikerusoft.citizens.model

import com.mikerusoft.citizens.model.Person.FilterBlankString

case class Address(country: String, city: String, street: String, buildingNo: Option[String], apartment: Option[String],
                                        entrance: Option[String], neighborhood: Option[String])
object Address {
  def builder() = new Builder()

  class Builder(var country: Option[String], var city: Option[String], var street: Option[String], var buildingNo: Option[String],
                       var apartment: Option[String], var entrance: Option[String], var neighborhood: Option[String], var built: Boolean = false) {
    def this() = this(None, None, None, None, None, None, None)

    def country(country: String): Builder = { this.country = Option(country).filterNotEmpty(); built = true; this }
    def city(city: String): Builder = { this.city = Option(city).filterNotEmpty(); built = true;; this }
    def street(street: String): Builder = { this.street = Option(street).filterNotEmpty(); built = true;; this }
    def buildingNo(buildingNo: String): Builder = { this.buildingNo = Option(buildingNo).filterNotEmpty(); built = true;; this }
    def apartment(apartment: String): Builder = { this.apartment = Option(apartment).filterNotEmpty(); built = true;; this }
    def entrance(entrance: String): Builder = { this.entrance = Option(entrance).filterNotEmpty(); built = true;; this }
    def neighborhood(neighborhood: String): Builder = { this.neighborhood = Option(neighborhood).filterNotEmpty(); built = true;; this }

    def build(): Option[Address] = if (built) Option(Address(country.get, city.get, street.get, buildingNo, apartment, entrance, neighborhood)) else None
  }
}

