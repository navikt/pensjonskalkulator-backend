package no.nav.pensjon.kalkulator.ansatt.enhet.api.v1.acl

import no.nav.pensjon.kalkulator.ansatt.enhet.AnsattEnhetResult
import no.nav.pensjon.kalkulator.ansatt.enhet.TjenestekontorEnhet
import no.nav.pensjon.kalkulator.validity.Problem

object ResultMapper {

    fun toDto(source: AnsattEnhetResult) =
        AnsattEnhetV1Result(
            enhetListe = source.enhetListe.map(::tjenestekontor),
            problem = source.problem?.let(::problem)
        )

    private fun tjenestekontor(source: TjenestekontorEnhet) =
        AnsattEnhetV1Tjenestekontor(
            id = source.id,
            navn = source.navn
        )

    private fun problem(source: Problem) =
        AnsattEnhetV1Problem(
            kode = AnsattEnhetV1ProblemType.entries.firstOrNull { it.internalValue == source.type }
                ?: AnsattEnhetV1ProblemType.SERVERFEIL,
            beskrivelse = source.beskrivelse
        )
}