package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening

@JsonIgnoreProperties(ignoreUnknown = true)
data class PoppPensjonspoeng(
    val pensjonspoengId: Long? = null,
    val fnr: String? = null,
    val fnrOmsorgFor: String? = null,
    val kilde: String? = null,
    val pensjonspoengType: String? = null,
    val inntekt: PoppPensjonspoengInntekt? = null,
    val omsorg: PoppPensjonspoengOmsorg? = null,
    val ar: Int? = null,
    val anvendtPi: Int? = null,
    val poeng: Double? = null,
    val maxUforegrad: Int? = null
) {
    fun toInternalValue(): AarligOpptjening? =
        ar?.let {
            AarligOpptjening(
                aar = it,
                pensjonsgivendeInntekt = inntekt?.belop?.toInt() ?: 0,
                pensjonspoeng = poeng ?: 0.0,
                pensjonspoengType = PoppPensjonspoengType.toInternalValue(externalValue = pensjonspoengType),
                maksimalUfoeregrad = maxUforegrad,
                omsorgspoeng = omsorg?.ar, //TODO er denne riktig? (ble innført i pensjonskalkulator-backend PR 471)
                beholdning = 0, // not available here
                merknadListe = emptyList() // not available here
            )
        }
}