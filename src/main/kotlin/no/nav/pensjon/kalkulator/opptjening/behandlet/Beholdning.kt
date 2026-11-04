package no.nav.pensjon.kalkulator.opptjening.behandlet

/**
 * Inneholder pensjonsbeholdning i form av et beløp i norske kroner.
 * Beløpet representerer den "behandlede" verdien der et eventuelt pensjonsuttak er hensyntatt
 * (altså ikke den "rå" verdien fra opptjeningsregisteret).
 */
data class Beholdning(
    val beloep: Int
)