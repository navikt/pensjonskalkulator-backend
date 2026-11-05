package no.nav.pensjon.kalkulator.opptjening.behandlet.client.backend.acl

import no.nav.pensjon.kalkulator.opptjening.behandlet.Beholdning

/**
 * Korresponderer med BeholdningV1 i pensjon-selvbetjening-opptjening-backend, ref.
 * github.com/navikt/pensjon-selvbetjening-opptjening-backend/.../api/opptjening/v1/acl/BeholdningV1.kt
 */
data class OpptjeningBeholdning(
    val beloep: Int
) {
    fun toInternalValue() =
        Beholdning(beloep = beloep)
}