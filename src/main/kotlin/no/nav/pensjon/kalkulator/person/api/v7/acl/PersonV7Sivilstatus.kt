package no.nav.pensjon.kalkulator.person.api.v7.acl

import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.api.v7.acl.EnumUtil.missingExternalValue

/**
 * Using the prefix 'PersonV7' to avoid name clash with other DTOs (which causes problems in the generated Swagger API
 * documentation).
 * An alternative is to use 'springdoc.use-fqn=true', but this causes problems for the frontend's type checker (which
 * cannot handle DTO names with dots).
 */
enum class PersonV7Sivilstatus(val internalValue: Sivilstatus) {

    UNKNOWN(Sivilstatus.UNKNOWN),
    UOPPGITT(Sivilstatus.UOPPGITT),
    UGIFT(Sivilstatus.UGIFT),
    GIFT(Sivilstatus.GIFT),
    ENKE_ELLER_ENKEMANN(Sivilstatus.ENKE_ELLER_ENKEMANN),
    SKILT(Sivilstatus.SKILT),
    SEPARERT(Sivilstatus.SEPARERT),
    REGISTRERT_PARTNER(Sivilstatus.REGISTRERT_PARTNER),
    SEPARERT_PARTNER(Sivilstatus.SEPARERT_PARTNER),
    SKILT_PARTNER(Sivilstatus.SKILT_PARTNER),
    GJENLEVENDE_PARTNER(Sivilstatus.GJENLEVENDE_PARTNER),
    SAMBOER(Sivilstatus.SAMBOER);

    companion object {
        fun fromInternalValue(value: Sivilstatus) =
            entries.singleOrNull { it.internalValue == value } ?: missingExternalValue(type = "sivilstatus", value)
    }
}
