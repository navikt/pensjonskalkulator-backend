package no.nav.pensjon.kalkulator.person.api.intern.v1.acl

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.api.intern.v1.acl.EnumUtil.missingExternalValue

/**
 * Transferable representation (data transfer object) of 'sivilstand'.
 * Used in version 1 of the 'person' service for 'intern kalkulator'.
 * -----
 * Using the prefix 'PersonInternV1' to avoid name clash with other DTOs (which causes problems in the generated
 * Swagger API documentation).
 * An alternative is to use 'springdoc.use-fqn=true', but this causes problems for the frontend's type checker (which
 * cannot handle DTO names with dots).
 */
enum class PersonInternV1Sivilstand(val internalValue: Sivilstand) {
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
    UNKNOWN(internalValue = Sivilstand.UNKNOWN);

    companion object {
        private val log = KotlinLogging.logger {}

        fun fromInternalValue(value: Sivilstand): PersonInternV1Sivilstand =
            if (value == Sivilstand.SAMBOER)
                UGIFT.also { log.warn { "mapped sivilstand SAMBOER" } }
            else
                entries.singleOrNull { it.internalValue == value } ?: missingExternalValue(type = "sivilstand", value)
    }
}