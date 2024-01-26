package no.nav.pensjon.kalkulator.ekskludering.api.dto

import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak

enum class EkskluderingAarsakV1(val internalValue: EkskluderingAarsak) {

    NONE(EkskluderingAarsak.NONE),
    HAR_LOEPENDE_UFOERETRYGD(EkskluderingAarsak.HAR_LOEPENDE_UFOERETRYGD),
    HAR_GJENLEVENDEYTELSE(EkskluderingAarsak.HAR_GJENLEVENDEYTELSE),
    ER_APOTEKER(EkskluderingAarsak.ER_APOTEKER);

    companion object {
        private val values = EkskluderingAarsakV1.entries.toTypedArray()

        fun fromInternalValue(value: EkskluderingAarsak) = values.singleOrNull { it.internalValue == value } ?: NONE
    }
}
