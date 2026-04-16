package no.nav.pensjon.kalkulator.ansatt.enhet.client

import no.nav.pensjon.kalkulator.ansatt.enhet.TjenestekontorEnheter

interface EnhetClient {
    fun fetchTjenestekontorEnhetListe(ansattId: String): TjenestekontorEnheter
}
