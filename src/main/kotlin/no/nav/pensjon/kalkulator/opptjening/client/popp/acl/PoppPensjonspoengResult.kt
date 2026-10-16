package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening

@JsonIgnoreProperties(ignoreUnknown = true)
data class PoppPensjonspoengResult(
    val pensjonspoeng: List<PoppPensjonspoeng>? = null
) {
    fun toInternalValue(): List<AarligOpptjening> =
        pensjonspoeng.orEmpty().mapNotNull { it.toInternalValue() }
}