package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl

import jakarta.validation.constraints.NotNull

/**
 * Using the prefix 'EpsV1' to avoid name clash with other DTOs (which causes problems in the generated Swagger API
 * documentation).
 * An alternative is to use 'springdoc.use-fqn=true', but this causes problems for the frontend's type checker (which
 * cannot handle DTO names with dots).
 */
data class EpsV1SivilstatusResult(
    @field:NotNull val sivilstatus: EpsV1Sivilstatus
)