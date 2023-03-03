package no.nav.pensjon.kalkulator.opptjening.client.regler.dto

import java.util.*

data class OpptjeningRequestDto(
    val personOpptjeningsgrunnlagListe: List<AldersopptjeningDto>
)

data class AldersopptjeningDto(
    val opptjening: PensjonsgivendeInntektDto,
    val fodselsdato: Date
)

data class PensjonsgivendeInntektDto(
    val ar: Int,
    val pi: Int,
    val opptjeningType: OpptjeningstypeDto
)

data class OpptjeningstypeDto(
    val kode: String
)
