package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.map

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.representasjon.Personalia
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon
import no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.dto.PensjonRepresentasjonResult

object PensjonRepresentasjonMapper {

    fun fromDto(source: PensjonRepresentasjonResult) =
        (source.hasValidRepresentasjonsforhold == true).let {
            Representasjon(
                isValid = it,
                fullmaktsgiver = if (it) personalia(source) else null
            )
        }

    private fun personalia(source: PensjonRepresentasjonResult) =
        Personalia(
            navn = source.fullmaktsgiverNavn ?: "(ukjent)",
            pid = source.fullmaktsgiverFnr?.let(::Pid)
                ?: throw IllegalArgumentException("Manglende PID for gyldig representasjon")
        )
}