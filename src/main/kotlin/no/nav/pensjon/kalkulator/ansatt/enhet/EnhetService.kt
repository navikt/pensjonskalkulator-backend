package no.nav.pensjon.kalkulator.ansatt.enhet

import no.nav.pensjon.kalkulator.ansatt.enhet.client.EnhetClient
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import org.springframework.stereotype.Service

@Service
class EnhetService(
    private val client: EnhetClient,
    private val ansattIdGetter: SecurityContextNavIdExtractor
) {
    fun tjenestekontorEnhetListe(): AnsattEnhetResult =
        client.fetchTjenestekontorEnhetListe(ansattId = ansattIdGetter.id())
}
