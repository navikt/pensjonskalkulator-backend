package no.nav.pensjon.kalkulator.ekskludering

import no.nav.pensjon.kalkulator.sak.SakType

enum class EkskluderingAarsak(val sakType: SakType) {

    NONE(SakType.NONE),
    HAR_LOEPENDE_UFOERETRYGD(SakType.UFOERETRYGD),
    HAR_GJENLEVENDEYTELSE(SakType.GJENLEVENDEYTELSE),
    ER_APOTEKER(SakType.NONE);

    companion object {
        private val values = EkskluderingAarsak.entries.toTypedArray()

        fun from(sakType: SakType) = values.singleOrNull { it.sakType == sakType } ?: NONE
    }
}
