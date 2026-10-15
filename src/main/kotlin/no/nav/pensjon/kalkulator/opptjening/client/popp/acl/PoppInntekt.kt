package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import java.math.BigDecimal

data class PoppInntekt(
    val inntektId: Long? = null,
    val fnr: String? = null,
    val kilde: String? = null,
    val kommune: String? = null,
    val piMerke: String? = null,
    val inntektAr: Int? = null,
    val belop: Long? = null,
    val inntektType: String? = null
) {
    fun toInternalValue() =
        Inntekt(
            type = inntektType?.let(Opptjeningstype::forCode) ?: Opptjeningstype.OTHER,
            aar = inntektAr ?: 0,
            beloep = belop?.let(::BigDecimal) ?: BigDecimal.ZERO
        )
}