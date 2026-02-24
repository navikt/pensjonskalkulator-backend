package no.nav.pensjon.kalkulator.person.api.intern.v1.acl

import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.api.intern.v1.acl.EnumUtil.missingExternalValue

enum class SivilstatusDto(val internalValue: Sivilstatus) {
    UNKNOWN(internalValue = Sivilstatus.UNKNOWN),
    UOPPGITT(internalValue = Sivilstatus.UOPPGITT),
    UGIFT(internalValue = Sivilstatus.UGIFT),
    GIFT(internalValue = Sivilstatus.GIFT),
    ENKE_ELLER_ENKEMANN(internalValue = Sivilstatus.ENKE_ELLER_ENKEMANN),
    SKILT(internalValue = Sivilstatus.SKILT),
    SEPARERT(internalValue = Sivilstatus.SEPARERT),
    REGISTRERT_PARTNER(internalValue = Sivilstatus.REGISTRERT_PARTNER),
    SEPARERT_PARTNER(internalValue = Sivilstatus.SEPARERT_PARTNER),
    SKILT_PARTNER(internalValue = Sivilstatus.SKILT_PARTNER),
    GJENLEVENDE_PARTNER(internalValue = Sivilstatus.GJENLEVENDE_PARTNER),
    SAMBOER(internalValue = Sivilstatus.SAMBOER);

    companion object {
        fun fromInternalValue(value: Sivilstatus): SivilstatusDto =
            entries.singleOrNull { it.internalValue == value } ?: missingExternalValue(type = "sivilstatus", value)
    }
}