package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl

import io.swagger.v3.oas.annotations.media.Schema
import no.nav.pensjon.kalkulator.common.api.acl.CommonV1Sivilstatus
import no.nav.pensjon.kalkulator.person.Sivilstand

/**
 * Using the prefix 'EpsV1' to avoid name clash with other DTOs (which causes problems in the generated Swagger API
 * documentation).
 * An alternative is to use 'springdoc.use-fqn=true', but this causes problems for the frontend's type checker (which
 * cannot handle DTO names with dots).
 */
data class EpsV1EpsSpec(
    @field:Schema(description = "Sivilstand (i Folkeregisteret) - må angis hvis ikke sivilstatus angis")
    val sivilstand: EpsV1Sivilstand? = null,

    @field:Schema(description = "Sivilstatus (inkludert samboerskap) - brukes kun hvis sivilstand ikke angis")
    val sivilstatus: CommonV1Sivilstatus? = null,

    @field:Schema(description = "Bakgrunn for henting av opplysningene")
    val bakgrunn: String? = null
)

/**
 * Data transfer object (DTO) for sivilstand.
 */
enum class EpsV1Sivilstand(val internalValue: Sivilstand) {

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

    // Special value not in folkeregisterloven (used to represent unknown values):
    UNKNOWN(internalValue = Sivilstand.UNKNOWN)
}