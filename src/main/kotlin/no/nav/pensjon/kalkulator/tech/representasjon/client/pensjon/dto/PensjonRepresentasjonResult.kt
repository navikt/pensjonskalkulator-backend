package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.dto

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.representasjon.Personalia
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon

/**
 * Corresponds with RepresentasjonValidityV2 in pensjon-representasjon, ref.
 * github.com/navikt/pensjon-representasjon/src/main/kotlin/no/nav/pensjon/fullmakt/representasjon/model/RepresentasjonValidity
 */
data class PensjonRepresentasjonResult(
    val hasValidRepresentasjonsforhold: Boolean?,
    val representertNavn: String?,
    val representertPid: String?,
    val representertPidKryptert: String? = null
) {
    fun toInternalValue() =
        (hasValidRepresentasjonsforhold == true).let {
            Representasjon(
                isValid = it,
                fullmaktsgiver = if (it) personalia() else null
            )
        }

    private fun personalia() =
        Personalia(
            navn = representertNavn ?: "(ukjent)",
            pid = representertPid?.let(::Pid)
                ?: throw IllegalArgumentException("Manglende PID for gyldig representasjon")
        )
}