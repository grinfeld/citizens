package com.mikerusoft.citizens.model

import cats.data.Validated
import cats.data.Validated.Valid
import cats.implicits.{catsSyntaxTuple2Semigroupal, catsSyntaxTuple3Semigroupal}
import com.mikerusoft.citizens.model.Types.Validation
import com.mikerusoft.citizens.model.context.Validation._

case class Person(id: Option[Long], tz: Option[String], phones: List[Phone], emails: List[String], address: Option[Address], tags: List[String], remove: Boolean = false, personalInfo: PersonalInfo)

object Person {
  def builder(): Builder = Builder(None, List(), List(), Address.builder(), List(), remove = false, PersonalInfo.builder())

  case class Builder(var tz: Option[String], var phones: List[Phone.Builder], var emails: List[String], var address: Address.Builder,
                      var tags: List[String], var remove: Boolean, var personalInfo: PersonalInfo.Builder) {

    def withTz(tz: String): Builder = { copy(tz = Option(tz).filterNotEmpty()) }
    def withAddress(address: Address.Builder): Builder = { copy(address = address) }
    def withAddressField(updateFieldFunc: Address.Builder => Address.Builder): Builder = { copy(address = updateFieldFunc.apply(address)) }
    def withPhones(phones: List[Phone.Builder]): Builder = { copy(phones = phones) }
    def withEmails(emails: List[String]): Builder = { copy(emails = emails) }
    def withTags(tags: List[String]): Builder = { copy(tags = tags) }
    def withRemove(remove: Boolean): Builder = { copy(remove = remove) }
    def withPersonalInfo(personalInfo: PersonalInfo.Builder): Builder = { copy(personalInfo = personalInfo) }
    def withPersonalInfoField(personalInfoFieldFunc: PersonalInfo.Builder => PersonalInfo.Builder): Builder = { copy(personalInfo = personalInfoFieldFunc.apply(personalInfo)) }

    def buildWith() : Validation[Person] = {
      val phonesValidated: Validated[String, List[Phone]] = phones.map(_.buildWith()).foldLeft(Valid(List[Phone]()).asInstanceOf[Validation[List[Phone]]])((acc, ph) => (acc, ph).mapN((ls, p) => p :: ls))
      val personalInfoValidated = personalInfo.buildWith()
      val addressValidated = address.buildWith()
      (phonesValidated, personalInfoValidated, addressValidated).mapN((phones, personalInfo, address) =>
        Person(None, tz, phones, emails.filterNot(_.isBlank), address, tags.filterNot(_.isBlank), remove, personalInfo)
      )
    }
  }

  implicit class FilterBlankString(val value: Option[String]) {
    def filterNotEmpty(): Option[String] = value.map(_.trim).filterNot(v => v.isBlank)
  }
}