package no.nav.pensjon.kalkulator.ansatt.enhet

import no.nav.pensjon.kalkulator.validity.Problem

data class TjenestekontorEnheter(
    val enhetListe: List<TjenestekontorEnhet>,
    val problem: Problem? = null
)

data class TjenestekontorEnhet(
    val id: String,
    val navn: String,
    val nivaa: String
)