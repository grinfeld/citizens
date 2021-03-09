package com.mikerusoft.citizens.model

import cats.data.Validated
import cats.data.Validated.Valid
import cats.implicits.{catsSyntaxTuple2Semigroupal, catsSyntaxTuple3Semigroupal}
import com.mikerusoft.citizens.model.Types.ErrorMsg
import com.mikerusoft.citizens.model.context.Validation._

case class Person(tz: Option[String], phones: List[Phone], emails: List[String], address: Option[Address], tags: List[String],
                  remove: Boolean = false, personalInfo: PersonalInfo)

object Person {
  def builder() = new Builder()

  class Builder(var tz: Option[String], var phones: List[Phone.Builder], var emails: List[String], var address: Address.Builder,
                      var tags: List[String], var remove: Boolean, var personalInfo: PersonalInfo.Builder) {

    def this() = this(None, List(), List(), Address.builder(), List(), false, PersonalInfo.builder())

    def withTz(tz: String): Builder = { this.tz = Option(tz).filterNotEmpty(); this }
    def withAddress(address: Address.Builder): Builder = { this.address = address; this }
    def withAddress(updateFieldFunc: Address.Builder => Unit): Builder = { updateFieldFunc.apply(this.address); this }
    def withPhones(phones: List[Phone.Builder]): Builder = { this.phones = phones; this }
    def withEmails(emails: List[String]): Builder = { this.emails = emails; this }
    def withTags(tags: List[String]): Builder = { this.tags = tags; this }
    def withRemove(remove: Boolean): Builder = { this.remove = remove; this }
    def withPersonalInfo(personalInfo: PersonalInfo.Builder): Builder = { this.personalInfo = personalInfo; this }
    def withPersonalInfo(personalInfoFieldFunc: PersonalInfo.Builder => Any): Builder = { personalInfoFieldFunc.apply(this.personalInfo); this }

    def buildWith() : Validated[ErrorMsg, Person] = {
      val phonesValidated: Validated[String, List[Phone]] = phones.map(_.buildWith()).foldLeft(Valid(List[Phone]()).asInstanceOf[Validated[String, List[Phone]]])((acc, ph) => (acc, ph).mapN((ls, p) => p :: ls))
      val personalInfoValidated = personalInfo.buildWith()
      val addressValidated = address.buildWith()
      (phonesValidated, personalInfoValidated, addressValidated).mapN((phones, personalInfo, address) =>
        Person(tz, phones, emails.filterNot(_.isBlank), address, tags.filterNot(_.isBlank), remove, personalInfo)
      )
    }
  }

  implicit class FilterBlankString(val value: Option[String]) {
    def filterNotEmpty(): Option[String] = value.map(_.trim).filterNot(v => v.isBlank)
  }
}