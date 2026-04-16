package no.nav.pensjon.kalkulator.ansatt.enhet.client.navansatt.acl

import no.nav.pensjon.kalkulator.ansatt.enhet.TjenestekontorEnheter
import no.nav.pensjon.kalkulator.ansatt.enhet.TjenestekontorEnhet
import no.nav.pensjon.kalkulator.validity.Problem
import no.nav.pensjon.kalkulator.validity.ProblemType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

object NavEnhetResultMapper {

    fun fromDto(dto: List<NavEnhetResultDto>) =
        TjenestekontorEnheter(
            enhetListe = dto.map(::enhet)
        )

    fun fromDto(dto: NavEnhetProblemDto, httpStatus: HttpStatusCode?) =
        TjenestekontorEnheter(
            enhetListe = emptyList(),
            problem = Problem(
                type = httpStatus?.let(::problemType) ?: ProblemType.SERVERFEIL,
                beskrivelse = dto.message
            )
        )

    private fun enhet(dto: NavEnhetResultDto) =
        TjenestekontorEnhet(
            id = dto.id,
            navn = dto.navn,
            nivaa = dto.nivaa
        )

    private fun problemType(httpStatus: HttpStatusCode): ProblemType =
        if (httpStatus == HttpStatus.NOT_FOUND)
            ProblemType.PERSON_IKKE_FUNNET
        else
            ProblemType.ANNEN_KLIENTFEIL
}