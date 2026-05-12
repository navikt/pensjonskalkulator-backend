package no.nav.pensjon.kalkulator.simulering.client.simulator.acl.spec

import no.nav.pensjon.kalkulator.person.Sivilstand

enum class SivilstandSpecDto(val internalValue: Sivilstand) {
    ENKE(internalValue = Sivilstand.ENKE_ELLER_ENKEMANN),
    GIFT(internalValue = Sivilstand.GIFT),
    GJPA(internalValue = Sivilstand.GJENLEVENDE_PARTNER),
    NULL(internalValue = Sivilstand.UOPPGITT),
    REPA(internalValue = Sivilstand.REGISTRERT_PARTNER),
    SEPA(internalValue = Sivilstand.SEPARERT_PARTNER),
    SEPR(internalValue = Sivilstand.SEPARERT),
    SKIL(internalValue = Sivilstand.SKILT),
    SKPA(internalValue = Sivilstand.SKILT_PARTNER),
    UGIF(internalValue = Sivilstand.UGIFT);

    companion object {
        fun fromInternalValue(value: Sivilstand): SivilstandSpecDto =
            entries.singleOrNull { it.internalValue == value } ?: NULL
    }
}
