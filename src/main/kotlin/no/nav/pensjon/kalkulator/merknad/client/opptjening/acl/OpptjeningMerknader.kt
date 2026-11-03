package no.nav.pensjon.kalkulator.merknad.client.opptjening.acl

import no.nav.pensjon.kalkulator.merknad.Merknader
import kotlin.to

/**
 * Korresponderer med MerknaderV2 i pensjon-selvbetjening-opptjening-backend, ref.
 * github.com/navikt/pensjon-selvbetjening-opptjening-backend/.../api/merknad/v2/acl/MerknaderV2.kt
 */
data class OpptjeningMerknader(
    val merknaderPerAar: Map<Int, List<OpptjeningMerknad>>
) {
    fun toInternalValue() =
        Merknader(
            perAar = merknaderPerAar.map { (aar, merknader) ->
                aar to merknader.map { it.toInternalValue() }
            }.toMap()
        )
}