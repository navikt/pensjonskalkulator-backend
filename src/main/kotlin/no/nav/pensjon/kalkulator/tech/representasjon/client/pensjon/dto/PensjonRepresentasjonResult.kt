package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.dto

data class PensjonRepresentasjonResult(
    val hasValidRepresentasjonsforhold: Boolean?,
    val fullmaktsgiverNavn: String?
)
