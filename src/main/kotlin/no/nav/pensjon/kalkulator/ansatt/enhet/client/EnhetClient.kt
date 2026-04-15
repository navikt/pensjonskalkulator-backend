package no.nav.pensjon.kalkulator.ansatt.enhet.client

import no.nav.pensjon.kalkulator.ansatt.enhet.AnsattEnhetResult

interface EnhetClient {
    fun fetchTjenestekontorEnhetListe(ansattId: String): AnsattEnhetResult
}
