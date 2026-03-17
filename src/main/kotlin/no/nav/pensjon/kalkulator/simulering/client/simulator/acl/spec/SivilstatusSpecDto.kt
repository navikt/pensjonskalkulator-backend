package no.nav.pensjon.kalkulator.simulering.client.simulator.acl.spec

import no.nav.pensjon.kalkulator.person.Sivilstatus

enum class SivilstatusSpecDto(val internalValue: Sivilstatus) {
    ENKE_ELLER_ENKEMANN(internalValue = Sivilstatus.ENKE_ELLER_ENKEMANN),
    GIFT(internalValue = Sivilstatus.GIFT),
    //GIFT_LEVER_ADSKILT(internalValue = Sivilstatus.GIFT),
    //GJENLEVENDE_ETTER_SAMLIVSBRUDD(internalValue = Sivilstatus.GJENLEVENDE_PARTNER),
    GJENLEVENDE_PARTNER(internalValue = Sivilstatus.GJENLEVENDE_PARTNER),
    //GJENLEVENDE_SAMBOER(internalValue = Sivilstatus.GJENLEVENDE_PARTNER),
    REGISTRERT_PARTNER(internalValue = Sivilstatus.REGISTRERT_PARTNER),
    //REGISTRERT_PARTNER_LEVER_ADSKILT(internalValue = Sivilstatus.REGISTRERT_PARTNER),
    SAMBOER(internalValue = Sivilstatus.SAMBOER),
    SEPARERT(internalValue = Sivilstatus.SEPARERT),
    SEPARERT_PARTNER(internalValue = Sivilstatus.SEPARERT_PARTNER),
    SKILT(internalValue = Sivilstatus.SKILT),
    SKILT_PARTNER(internalValue = Sivilstatus.SKILT_PARTNER),
    UGIFT(internalValue = Sivilstatus.UGIFT),
    UKJENT(internalValue = Sivilstatus.UNKNOWN);

    companion object {
        fun fromInternalValue(value: Sivilstatus): SivilstatusSpecDto =
            entries.singleOrNull { it.internalValue == value } ?: UKJENT
    }
}
