package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PoppPensjonspoengOmsorg(
    val omsorgId: Long? = null,
    val ar: Int? = null,
    val omsorgType: String? = null
)