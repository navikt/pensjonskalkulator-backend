package no.nav.pensjon.kalkulator.opptjening.behandlet.client.backend.acl

import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.opptjening.behandlet.UttaksgradMerknad

/**
 * Korresponderer med UttaksgradV1 i pensjon-selvbetjening-opptjening-backend, ref.
 * github.com/navikt/pensjon-selvbetjening-opptjening-backend/.../api/opptjening/v1/acl/UttaksgradV1.kt
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