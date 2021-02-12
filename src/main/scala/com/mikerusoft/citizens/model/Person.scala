package com.mikerusoft.citizens.model

case class Person(tz: Option[String], phones: List[Phone], emails: List[String], address: Option[Address], tags: List[String],
                  remove: Boolean = false, personalInfo: PersonalInfo)

object Person {
  def builder() = new Builder()

  class Builder(var tz: Option[String], var phones: List[Phone.Builder], var emails: List[String], var address: Address.Builder,
                      var tags: List[String], var remove: Boolean, var personalInfo: PersonalInfo.Builder) {

    def this() = this(None, List(), List(), Address.builder(), List(), false, PersonalInfo.builder())

    def withTz(tz: String): Builder = { this.tz = Option(tz); this }
    def withAddress(address: Address.Builder): Builder = { this.address = address; this }
    def withAddress(updateFieldFunc: Address.Builder => Unit): Builder = { updateFieldFunc.apply(this.address); this }
    def withPhones(phones: List[Phone.Builder]): Builder = { this.phones = phones; this }
    def withEmails(emails: List[String]): Builder = { this.emails = emails; this }
    def withTags(tags: List[String]): Builder = { this.tags = tags; this }
    def withRemove(remove: Boolean): Builder = { this.remove = remove; this }
    def withPersonalInfo(personalInfo: PersonalInfo.Builder): Builder = { this.personalInfo = personalInfo; this }
    def withPersonalInfo(personalInfoFieldFunc: PersonalInfo.Builder => Any): Builder = { personalInfoFieldFunc.apply(this.personalInfo); this }

    def build(): Person = {
      Person(tz, phones.map(_.build()), emails.filterNot(_.isBlank), address.build(), tags.filterNot(_.isBlank), remove, personalInfo.build())
    }
  }
}

