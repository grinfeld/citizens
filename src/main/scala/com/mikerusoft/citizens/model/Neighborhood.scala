package com.mikerusoft.citizens.model

case class Neighborhood(id: Option[Int], name: String, street: List[Street], city: String, country: String)
case class Street(name: String, from: Int, to: Int)
