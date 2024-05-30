package no.nav.pensjon.kalkulator.ekskludering.api.dto

import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak

enum class EkskluderingAarsakV2(val internalValue: EkskluderingAarsak) {

    NONE(EkskluderingAarsak.NONE),
    ER_APOTEKER(EkskluderingAarsak.ER_APOTEKER);

    companion object {
        private val values = EkskluderingAarsakV2.entries.toTypedArray()

        fun fromInternalValue(value: EkskluderingAarsak) = values.singleOrNull { it.internalValue == value } ?: NONE
    }
}
