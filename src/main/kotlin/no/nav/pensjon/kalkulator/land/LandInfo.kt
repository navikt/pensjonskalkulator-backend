package no.nav.pensjon.kalkulator.land

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LandInfo(
    val landkode: String,
    val kravOmArbeid: Boolean? = null, // kun relevant for land med trygdeavtale
    val bokmaalNavn: String,
    val nynorskNavn: String,
    val engelskNavn: String
)
