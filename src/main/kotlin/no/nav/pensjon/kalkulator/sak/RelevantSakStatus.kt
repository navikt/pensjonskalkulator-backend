package no.nav.pensjon.kalkulator.sak

/**
 * Forteller om bruker har en sak som er relevant for bruken av kalkulatoren.
 */
data class RelevantSakStatus(val harSak: Boolean, val sakType: SakType)
