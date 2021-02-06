package com.mikerusoft.citizens.model

case class Address(country: String, city: String, street: String, buildingNo: Option[String], apartment: Option[String], entrance: Option[String], neighborhood: Option[String])