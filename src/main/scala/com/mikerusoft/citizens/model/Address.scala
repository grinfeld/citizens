package com.mikerusoft.citizens.model

case class Address(country: String, city: String, street: String, buildingNo: Option[String], apartment: Option[String],
                                        entrance: Option[String], neighborhood: Option[String])
object Address {
  def builder() = new Builder()

  class Builder(var country: Option[String], var city: Option[String], var street: Option[String], var buildingNo: Option[String],
                       var apartment: Option[String], var entrance: Option[String], var neighborhood: Option[String]) {
    def this() = this(None, None, None, None, None, None, None)

    def country(country: String): Builder = { this.country = Option(country); this }
    def city(city: String): Builder = { this.city = Option(city); this }
    def street(street: String): Builder = { this.street = Option(street); this }
    def buildingNo(buildingNo: String): Builder = { this.buildingNo = Option(buildingNo); this }
    def apartment(apartment: String): Builder = { this.apartment = Option(apartment); this }
    def entrance(entrance: String): Builder = { this.entrance = Option(entrance); this }
    def neighborhood(neighborhood: String): Builder = { this.neighborhood = Option(neighborhood); this }

    def build(): Address = Address(country.get, city.get, street.get, buildingNo, apartment, entrance, neighborhood)
  }
}

