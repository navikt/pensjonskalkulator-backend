package no.nav.pensjon.kalkulator.normalder.client

import no.nav.pensjon.kalkulator.normalder.Aldersgrenser

interface NormertPensjonsalderClient {
    fun fetchNormalderListe(): List<Aldersgrenser>
}
