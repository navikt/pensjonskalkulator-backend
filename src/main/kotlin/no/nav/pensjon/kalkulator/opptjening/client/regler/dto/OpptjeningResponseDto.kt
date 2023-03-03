package no.nav.pensjon.kalkulator.opptjening.client.regler.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

data class OpptjeningResponseDto(
    val personOpptjeningsgrunnlagListe: ArrayList<OpptjeningDto>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpptjeningDto(
    val opptjening: OpptjeningsdetaljerDto
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpptjeningsdetaljerDto(
    val ar: Int,
    val pia: Int,
    val pp: BigDecimal
)
