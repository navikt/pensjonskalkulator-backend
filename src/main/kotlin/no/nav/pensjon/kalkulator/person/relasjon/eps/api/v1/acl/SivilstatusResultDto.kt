package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl

import jakarta.validation.constraints.NotNull

data class SivilstatusResultDto(
    @field:NotNull val sivilstatus: SivilstatusDto
)