package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PoppPensjonspoengInntekt(
    val inntektId: Long? = null,
    val fnr: String? = null,
    val inntektAr: Int? = null,
    val kilde: String? = null,
    val kommune: String? = null,
    val piMerke: String? = null,
    val inntektType: String? = null,
    val belop: Long? = null
)