package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.person.Sivilstatus

/**
 * The 'externalValue' is sivilstatus values used by pensjonssimulator.
 * pensjonssimulator supports the concept of 'samboer' (unlike Folkeregisteret/PDL).
 */
enum class SimulatorSivilstatus(val externalValue: String, val internalValue: Sivilstatus) {

    ENKE_ELLER_ENKEMANN(externalValue = "ENKE", internalValue = Sivilstatus.ENKE_ELLER_ENKEMANN),
    GIFT(externalValue = "GIFT", internalValue = Sivilstatus.GIFT),
    GIFT_LEVER_ADSKILT(externalValue = "GLAD", internalValue = Sivilstatus.GIFT),
    GJENLEVENDE_PARTNER(externalValue = "GJPA", internalValue = Sivilstatus.GJENLEVENDE_PARTNER),
    REGISTRERT_PARTNER(externalValue = "REPA", internalValue = Sivilstatus.REGISTRERT_PARTNER),
    REGISTRERT_PARTNER_LEVER_ADSKILT(externalValue = "PLAD", internalValue = Sivilstatus.REGISTRERT_PARTNER),
    SAMBOER(externalValue = "SAMB", internalValue = Sivilstatus.SAMBOER),
    SEPARERT(externalValue = "SEPR", internalValue = Sivilstatus.SEPARERT),
    SEPARERT_PARTNER(externalValue = "SEPA", internalValue = Sivilstatus.SEPARERT_PARTNER),
    SKILT(externalValue = "SKIL", internalValue = Sivilstatus.SKILT),
    SKILT_PARTNER(externalValue = "SKPA", internalValue = Sivilstatus.SKILT_PARTNER),
    UGIFT(externalValue = "UGIF", internalValue = Sivilstatus.UGIFT),
    GJENLEVENDE_ETTER_SAMLIVSBRUDD(externalValue = "GJES", internalValue = Sivilstatus.UOPPGITT),
    GJENLEVENDE_SAMBOER(externalValue = "GJSA", internalValue = Sivilstatus.UOPPGITT),
    UDEFINERT(externalValue = "NULL", internalValue = Sivilstatus.UOPPGITT);

    companion object {
        fun fromInternalValue(sivilstatus: Sivilstatus?): SimulatorSivilstatus =
            when (sivilstatus) {
                Sivilstatus.UOPPGITT -> UDEFINERT // ambiguous UDEFINERT/GJENLEVENDE_*
                Sivilstatus.GIFT -> GIFT // ambiguous GIFT/-_LEVER_ADSKILT
                Sivilstatus.REGISTRERT_PARTNER -> REGISTRERT_PARTNER // ambiguous REGISTRERT_PARTNER/-_LEVER_ADSKILT
                else -> entries.firstOrNull { it.internalValue == sivilstatus } ?: UDEFINERT
            }
    }
}
