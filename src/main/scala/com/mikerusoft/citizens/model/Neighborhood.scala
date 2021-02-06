package com.mikerusoft.citizens.model

case class Neighborhood(name: String, street: List[Street])
case class Street(name: String, from: Int, to: Int)
