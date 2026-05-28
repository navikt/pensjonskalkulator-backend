package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map

import no.nav.pensjon.kalkulator.person.Sivilstatus

enum class SivilstatusTypeDto(val internalValue: Sivilstatus) {
    ENKE(internalValue = Sivilstatus.ENKE_ELLER_ENKEMANN),
    GIFT(internalValue = Sivilstatus.GIFT),
    GJPA(internalValue = Sivilstatus.GJENLEVENDE_PARTNER),
    REPA(internalValue = Sivilstatus.REGISTRERT_PARTNER),
    SAMB(internalValue = Sivilstatus.SAMBOER),
    SEPA(internalValue = Sivilstatus.SEPARERT_PARTNER),
    SEPR(internalValue = Sivilstatus.SEPARERT),
    SKIL(internalValue = Sivilstatus.SKILT),
    SKPA(internalValue = Sivilstatus.SKILT_PARTNER),
    UGIF(internalValue = Sivilstatus.UGIFT),
    NULL(internalValue = Sivilstatus.UNKNOWN);

    companion object {
        fun fromInternalValue(value: Sivilstatus): SivilstatusTypeDto =
            entries.singleOrNull { it.internalValue == value } ?: NULL
    }
}
