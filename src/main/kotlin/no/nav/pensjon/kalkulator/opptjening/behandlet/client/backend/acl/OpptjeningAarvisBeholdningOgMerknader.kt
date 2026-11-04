package no.nav.pensjon.kalkulator.opptjening.behandlet.client.backend.acl

import no.nav.pensjon.kalkulator.opptjening.behandlet.AarvisBeholdningOgMerknader

/**
 * Korresponderer med AarvisBeholdningOgMerknaderV1 i pensjon-selvbetjening-opptjening-backend, ref.
 * github.com/navikt/pensjon-selvbetjening-opptjening-backend/.../api/opptjening/v1/acl/AarvisBeholdningOgMerknaderV1.kt
 */
data class OpptjeningAarvisBeholdningOgMerknader(
    val perAar: Map<Int, OpptjeningBeholdningOgMerknader>
) {
    fun toInternalValue() =
        AarvisBeholdningOgMerknader(
            perAar = perAar.map { (aar, data) -> aar to data.toInternalValue() }.toMap()
        )
}