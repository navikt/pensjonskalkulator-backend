package no.nav.pensjon.kalkulator.opptjening.client.popp.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PensjonspoengResponseDto(
    val pensjonspoeng: List<PensjonspoengDto>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PensjonspoengDto(
    val pensjonspoengId: Long? = null,
    val fnr: String? = null,
    val fnrOmsorgFor: String? = null,
    val kilde: String? = null,
    val pensjonspoengType: String? = null,
    val inntekt: PensjonspoengInntektDto? = null,
    val omsorg: PensjonspoengOmsorgDto? = null,
    val ar: Int? = null,
    val anvendtPi: Int? = null,
    val poeng: Double? = null,
    val maxUforegrad: Int? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PensjonspoengInntektDto(
    val inntektId: Long? = null,
    val fnr: String? = null,
    val inntektAr: Int? = null,
    val kilde: String? = null,
    val kommune: String? = null,
    val piMerke: String? = null,
    val inntektType: String? = null,
    val belop: Long? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PensjonspoengOmsorgDto(
    val omsorgId: Long? = null,
    val ar: Int? = null,
    val omsorgType: String? = null
)
