package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

data class PoppPensjonspoengSpec(
    val fnr: String,
    val fomAr: Int? = null,
    val tomAr: Int? = null,
    val pensjonspoengType: String? = null
)