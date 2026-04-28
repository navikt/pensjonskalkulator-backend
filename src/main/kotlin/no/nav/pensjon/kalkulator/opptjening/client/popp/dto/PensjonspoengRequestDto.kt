package no.nav.pensjon.kalkulator.opptjening.client.popp.dto

data class PensjonspoengRequestDto(
    val fnr: String,
    val fomAr: Int? = null,
    val tomAr: Int? = null,
    val pensjonspoengType: String? = null
)
