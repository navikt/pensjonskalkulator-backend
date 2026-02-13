package no.nav.pensjon.kalkulator.simulering.client.simulator.acl.spec

import no.nav.pensjon.kalkulator.person.Sivilstand

enum class SivilstatusSpecDto(val internalValue: Sivilstand) {
    ENKE_ELLER_ENKEMANN(internalValue = Sivilstand.ENKE_ELLER_ENKEMANN),
    GIFT(internalValue = Sivilstand.GIFT),
    //GIFT_LEVER_ADSKILT(internalValue = Sivilstand.GIFT),
    //GJENLEVENDE_ETTER_SAMLIVSBRUDD(internalValue = Sivilstand.GJENLEVENDE_PARTNER),
    GJENLEVENDE_PARTNER(internalValue = Sivilstand.GJENLEVENDE_PARTNER),
    //GJENLEVENDE_SAMBOER(internalValue = Sivilstand.GJENLEVENDE_PARTNER),
    REGISTRERT_PARTNER(internalValue = Sivilstand.REGISTRERT_PARTNER),
    //REGISTRERT_PARTNER_LEVER_ADSKILT(internalValue = Sivilstand.REGISTRERT_PARTNER),
    SAMBOER(internalValue = Sivilstand.SAMBOER),
    SEPARERT(internalValue = Sivilstand.SEPARERT),
    SEPARERT_PARTNER(internalValue = Sivilstand.SEPARERT_PARTNER),
    SKILT(internalValue = Sivilstand.SKILT),
    SKILT_PARTNER(internalValue = Sivilstand.SKILT_PARTNER),
    UGIFT(internalValue = Sivilstand.UGIFT),
    UKJENT(internalValue = Sivilstand.UNKNOWN);

    companion object {
        private val values = entries.toTypedArray()

        fun fromInternalValue(value: Sivilstand): SivilstatusSpecDto =
            values.singleOrNull { it.internalValue == value } ?: UKJENT
    }
}
