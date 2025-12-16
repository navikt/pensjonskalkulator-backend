package no.nav.pensjon.kalkulator.land

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import jakarta.validation.constraints.NotNull

@JsonInclude(NON_NULL)
data class LandInfo(
    @field:NotNull val landkode: String,
    val kravOmArbeid: Boolean? = null, // kun relevant for land med trygdeavtale
    @field:NotNull val bokmaalNavn: String,
    @field:NotNull val nynorskNavn: String,
    @field:NotNull val engelskNavn: String
)
