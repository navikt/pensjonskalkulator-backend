package no.nav.pensjon.kalkulator.vedtak.client.pen.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PenInnledningsdataDto(
    @JsonProperty("isGjenlevenderett")
    val isGjenlevenderett: Boolean
)
