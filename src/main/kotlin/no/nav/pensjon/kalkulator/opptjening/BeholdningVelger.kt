package no.nav.pensjon.kalkulator.opptjening

import mu.KotlinLogging

object BeholdningVelger {

    private val log = KotlinLogging.logger {}

    fun velg(beholdningListe: List<DatertBeholdning>): DatertBeholdning? =
        beholdningListe.maxByOrNull { it.dato }?.also {
            if (beholdningListe.size > 1) log.debug { "valgte beholdning $it fra $beholdningListe" }
        }
}