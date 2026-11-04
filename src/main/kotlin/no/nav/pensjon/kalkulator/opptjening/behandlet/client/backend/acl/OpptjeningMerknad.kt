package no.nav.pensjon.kalkulator.opptjening.behandlet.client.backend.acl

import no.nav.pensjon.kalkulator.opptjening.behandlet.Merknad

/**
 * Korresponderer med MerknadV1 i pensjon-selvbetjening-opptjening-backend, ref.
 * github.com/navikt/pensjon-selvbetjening-opptjening-backend/.../api/opptjening/v1/acl/MerknadV1.kt
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