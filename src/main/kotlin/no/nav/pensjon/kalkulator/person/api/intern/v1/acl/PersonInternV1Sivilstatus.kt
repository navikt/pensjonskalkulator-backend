package no.nav.pensjon.kalkulator.person.api.intern.v1.acl

import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.api.intern.v1.acl.EnumUtil.missingExternalValue

/**
 * Using the prefix 'PersonInternV1' to avoid name clash with other DTOs (which causes problems in the generated
 * Swagger API documentation).
 * An alternative is to use 'springdoc.use-fqn=true', but this causes problems for the frontend's type checker (which
 * cannot handle DTO names with dots).
 */
enum class PersonInternV1Sivilstatus(val internalValue: Sivilstatus) {
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
        fun fromInternalValue(value: Sivilstatus): PersonInternV1Sivilstatus =
            entries.singleOrNull { it.internalValue == value } ?: missingExternalValue(type = "sivilstatus", value)
    }
}