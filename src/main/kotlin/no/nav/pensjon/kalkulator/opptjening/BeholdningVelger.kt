package no.nav.pensjon.kalkulator.opptjening

object BeholdningVelger {

    fun velg(beholdningListe: List<DatertBeholdning>): DatertBeholdning? =
        beholdningListe.maxByOrNull { it.dato }
}