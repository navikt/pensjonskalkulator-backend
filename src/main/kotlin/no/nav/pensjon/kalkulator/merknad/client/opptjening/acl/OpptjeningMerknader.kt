package no.nav.pensjon.kalkulator.merknad.client.opptjening.acl

import no.nav.pensjon.kalkulator.merknad.Merknader
import kotlin.to

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