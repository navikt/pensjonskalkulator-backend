package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl

import no.nav.pensjon.kalkulator.person.Sivilstand

data class EpsSpecDto(
    val sivilstatus: SivilstatusDto
)

enum class SivilstatusDto(val internalValue: Sivilstand) {
    UNKNOWN(internalValue = Sivilstand.UNKNOWN),
    UOPPGITT(internalValue = Sivilstand.UOPPGITT),
    UGIFT(internalValue = Sivilstand.UGIFT),
    GIFT(internalValue = Sivilstand.GIFT),
    ENKE_ELLER_ENKEMANN(internalValue = Sivilstand.ENKE_ELLER_ENKEMANN),
    SKILT(internalValue = Sivilstand.SKILT),
    SEPARERT(internalValue = Sivilstand.SEPARERT),
    REGISTRERT_PARTNER(internalValue = Sivilstand.REGISTRERT_PARTNER),
    SEPARERT_PARTNER(internalValue = Sivilstand.SEPARERT_PARTNER),
    SKILT_PARTNER(internalValue = Sivilstand.SKILT_PARTNER),
    GJENLEVENDE_PARTNER(internalValue = Sivilstand.GJENLEVENDE_PARTNER),
    SAMBOER(internalValue = Sivilstand.SAMBOER)
}
