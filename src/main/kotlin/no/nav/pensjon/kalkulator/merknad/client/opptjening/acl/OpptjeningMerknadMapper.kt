package no.nav.pensjon.kalkulator.merknad.client.opptjening.acl

import no.nav.pensjon.kalkulator.merknad.Merknader

object OpptjeningMerknadMapper {

    fun fromDto(source: OpptjeningMerknader) =
        Merknader(
            perAar = source.merknaderPerAar.map { (aar, code) ->
                aar to code.map(OpptjeningMerknadCode::internalValue)
            }.toMap()
        )
}