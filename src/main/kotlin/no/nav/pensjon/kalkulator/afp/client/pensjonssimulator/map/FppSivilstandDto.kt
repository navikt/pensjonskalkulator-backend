package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map

import no.nav.pensjon.kalkulator.person.Sivilstand

enum class FppSivilstandDto(val internalValue: Sivilstand) {
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
        fun fromInternalValue(value: Sivilstand): FppSivilstandDto =
            entries.singleOrNull { it.internalValue == value } ?: NULL
    }
}
