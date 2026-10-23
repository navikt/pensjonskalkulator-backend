package no.nav.pensjon.kalkulator.merknad.client.opptjening.acl

import no.nav.pensjon.kalkulator.merknad.Merknad

data class OpptjeningMerknad(
    val kode: String,
    val uttaksgrad: OpptjeningUttaksgrad?
) {
    fun toInternalValue() =
        Merknad(
            kode = OpptjeningMerknadkode.internalValue(kode),
            uttaksgrad = uttaksgrad?.toInternalValue()
        )
}