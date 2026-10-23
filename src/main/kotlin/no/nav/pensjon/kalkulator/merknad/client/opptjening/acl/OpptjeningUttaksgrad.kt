package no.nav.pensjon.kalkulator.merknad.client.opptjening.acl

import no.nav.pensjon.kalkulator.merknad.UttaksgradMerknad

data class OpptjeningUttaksgrad(
    val grad: Int,
    val kalendermaanedFom: Int
) {
    fun toInternalValue() =
        UttaksgradMerknad(
            grad = grad,
            kalendermaanedFom = kalendermaanedFom
        )
}