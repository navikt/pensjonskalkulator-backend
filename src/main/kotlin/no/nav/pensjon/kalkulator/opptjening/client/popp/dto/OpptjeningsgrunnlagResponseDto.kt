package no.nav.pensjon.kalkulator.opptjening.client.popp.dto

data class OpptjeningsgrunnlagResponseDto(val opptjeningsGrunnlag: OpptjeningsgrunnlagDto)

data class OpptjeningsgrunnlagDto(var inntektListe: List<InntektDto>)

data class InntektDto(val inntektType: String, val inntektAr: Int, val belop: Long)
