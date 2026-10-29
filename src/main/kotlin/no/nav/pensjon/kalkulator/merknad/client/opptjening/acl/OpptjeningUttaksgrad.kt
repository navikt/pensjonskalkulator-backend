package no.nav.pensjon.kalkulator.merknad.client.opptjening.acl

import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.merknad.UttaksgradMerknad

/**
 * Korresponderer med UttaksgradV2 i pensjon-selvbetjening-opptjening-backend, ref.
 * github.com/navikt/pensjon-selvbetjening-opptjening-backend/.../api/merknad/v2/acl/MerknaderV2.kt
 */
data class OpptjeningUttaksgrad(
    val grad: Int,
    val kalendermaanedFom: Int? = null
) {
    fun toInternalValue() =
        UttaksgradMerknad(
            grad = Uttaksgrad.from(prosentsats = grad),
            kalendermaanedFom = kalendermaanedFom
        )
}