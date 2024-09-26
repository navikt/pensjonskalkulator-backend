package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.map

import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon
import no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.dto.PensjonRepresentasjonResult

object PensjonRepresentasjonMapper {

    fun fromDto(source: PensjonRepresentasjonResult) =
        Representasjon(
            isValid = source.hasValidRepresentasjonsforhold == true,
            fullmaktGiverNavn = source.fullmaktsgiverNavn ?: ""
        )
}
