package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.general.Aarlig
import java.time.LocalDate

/**
 * Pensjonsbeholdning på en gitt dato.
 */
data class DatertBeholdning(
    val dato: LocalDate,
    val beholdning: Int,
    val oppdateringsaarsak: Beholdningsoppdateringsaarsak = Beholdningsoppdateringsaarsak.NY_OPPTJENING
) : Aarlig {
    override val aar: Int = dato.year
}

enum class Beholdningsoppdateringsaarsak {
    NY_OPPTJENING,
    REGULERING,
    VEDTAK,
    UNKNOWN
}