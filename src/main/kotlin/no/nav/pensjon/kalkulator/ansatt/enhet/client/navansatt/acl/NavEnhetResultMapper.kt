package no.nav.pensjon.kalkulator.ansatt.enhet.client.navansatt.acl

import no.nav.pensjon.kalkulator.ansatt.enhet.AnsattEnhetResult
import no.nav.pensjon.kalkulator.ansatt.enhet.TjenestekontorEnhet
import no.nav.pensjon.kalkulator.validity.Problem
import no.nav.pensjon.kalkulator.validity.ProblemType

object NavEnhetResultMapper {

    fun fromDto(dto: List<NavEnhetResultDto>) =
        AnsattEnhetResult(
            enhetListe = dto.map(::enhet)
        )

    fun fromDto(dto: NavEnhetProblemDto) =
        AnsattEnhetResult(
            enhetListe = emptyList(),
            problem = Problem(type = ProblemType.PERSON_IKKE_FUNNET, beskrivelse = dto.message)
        )

    private fun enhet(dto: NavEnhetResultDto) =
        TjenestekontorEnhet(
            id = dto.id,
            navn = dto.navn,
            nivaa = dto.nivaa
        )
}