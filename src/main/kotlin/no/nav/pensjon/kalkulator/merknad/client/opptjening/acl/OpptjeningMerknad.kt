package no.nav.pensjon.kalkulator.merknad.client.opptjening.acl

import no.nav.pensjon.kalkulator.merknad.Merknad

/**
 * Korresponderer med MerknadV2 i pensjon-selvbetjening-opptjening-backend, ref.
 * github.com/navikt/pensjon-selvbetjening-opptjening-backend/.../api/merknad/v2/acl/MerknaderV2.kt
 */
data class OpptjeningMerknad(
    val kode: String,
    val uttaksgrad: OpptjeningUttaksgrad? = null
) {
    fun toInternalValue() =
        Merknad(
            kode = OpptjeningMerknadkode.internalValue(kode),
            uttaksgrad = uttaksgrad?.toInternalValue()
        )
}