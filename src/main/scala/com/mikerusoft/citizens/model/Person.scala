package com.mikerusoft.citizens.model

case class Person(id: Long, tz: Option[Long], phones: List[Phone], emails: List[String], address: Option[Address], tags: List[Tag], remove: Boolean, personalInfo: PersonalInfo)