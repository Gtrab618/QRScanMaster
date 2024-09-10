package com.gtrab.qrscanmaster.model.schema

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gtrab.qrscanmaster.extension.joinToStringNotNullOrBlankWithLineSeparator
import com.gtrab.qrscanmaster.extension.startsWithIgnoreCase
import ezvcard.Ezvcard
import ezvcard.VCardVersion
import ezvcard.property.Email
import ezvcard.property.StructuredName
import ezvcard.property.Telephone

data class VCard(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val emailType: String? =null,
    val phone: String? = null,
    val phoneType: String? = null,
    val secondaryPhone: String? = null,
    val secondaryPhoneType: String? = null,
    val tertiaryPhone: String? = null,
    val tertiaryPhoneType: String? = null,

) : Schema {

    companion object {
        private const val SCHEMA_PREFIX = "BEGIN:VCARD"
        private const val ADDRESS_SEPARATOR = ","

        fun parse(text: String): VCard? {
            if (text.startsWithIgnoreCase(SCHEMA_PREFIX).not()) {
                return null
            }

            try {
                val vCard= Ezvcard.parse(text).first() ?: return null

                val firstName = vCard.structuredName?.given
                val lastName = vCard.structuredName?.family
                var email : String? = null
                var emailType:String? = null
                var phone : String? =null
                var phoneType: String? = null
                var secondaryPhone: String? = null
                var secondaryPhoneType: String? = null
                var tertiaryPhone: String? = null
                var tertiaryPhoneType: String? = null


                vCard.emails?.getOrNull(0)?.apply {
                    email=value
                    emailType=types.getOrNull(0)?.value
                }

                vCard.telephoneNumbers?.getOrNull(0)?.apply {
                    phone = this.text
                    phoneType = types?.firstOrNull()?.value
                }

                vCard.telephoneNumbers?.getOrNull(1)?.apply {
                    secondaryPhone = this.text
                    secondaryPhoneType = types?.firstOrNull()?.value
                }

                vCard.telephoneNumbers?.getOrNull(2)?.apply {
                    tertiaryPhone = this.text
                    tertiaryPhoneType = types?.firstOrNull()?.value
                }

                return VCard(
                    firstName,
                    lastName,
                    email,
                    emailType,
                    phone,
                    phoneType,
                    secondaryPhone,
                    secondaryPhoneType,
                    tertiaryPhone,
                    tertiaryPhoneType
                )
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }

        }


    }

    override val schema = BarcodeSchema.VCARD

    override fun toFormattedText(): String {
        return listOf(
            "${firstName.orEmpty()} ${lastName.orEmpty()}",
            "${phone.orEmpty()} ${phoneType.orEmpty()}",
            "${secondaryPhone.orEmpty()} ${secondaryPhoneType.orEmpty()}",
            "${tertiaryPhone.orEmpty()} ${tertiaryPhoneType.orEmpty()}",
            "${email.orEmpty()} ${emailType.orEmpty()}"

        ).joinToStringNotNullOrBlankWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        val vCard= ezvcard.VCard()
        vCard.structuredName= StructuredName().apply {
            given= firstName
            family= lastName
        }

        if (email.isNullOrBlank().not()) {
            vCard.addEmail(Email(email))
        }

        if (phone.isNullOrBlank().not()) {
            vCard.addTelephoneNumber(Telephone(phone))
        }

        return Ezvcard
            .write(vCard)
            .version(VCardVersion.V4_0)
            .prodId(false)
            .go()
            .trimEnd('\n', '\r', ' ')

    }

}