package no.nav.pensjon.kalkulator.person.api.dto

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Sivilstand

enum class PersonSivilstandV4(val internalValue: Sivilstand) {

    UNKNOWN(Sivilstand.UNKNOWN),
    UOPPGITT(Sivilstand.UOPPGITT),
    UGIFT(Sivilstand.UGIFT),
    GIFT(Sivilstand.GIFT),
    ENKE_ELLER_ENKEMANN(Sivilstand.ENKE_ELLER_ENKEMANN),
    SKILT(Sivilstand.SKILT),
    SEPARERT(Sivilstand.SEPARERT),
    REGISTRERT_PARTNER(Sivilstand.REGISTRERT_PARTNER),
    SEPARERT_PARTNER(Sivilstand.SEPARERT_PARTNER),
    SKILT_PARTNER(Sivilstand.SKILT_PARTNER),
    GJENLEVENDE_PARTNER(Sivilstand.GJENLEVENDE_PARTNER);
    // No SAMBOER in this context

    companion object {
        private val values = entries.toTypedArray()
        private val log = KotlinLogging.logger {}

        fun fromInternalValue(value: Sivilstand?) =
            values.singleOrNull { it.internalValue == value } ?: default(value)

        private fun default(internalValue: Sivilstand?): PersonSivilstandV4 =
            internalValue?.let {
                log.warn { "Unknown sivilstand '$it'" }
                UNKNOWN
            } ?: UOPPGITT
    }
}
