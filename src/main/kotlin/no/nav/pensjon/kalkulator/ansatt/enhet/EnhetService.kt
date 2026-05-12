package no.nav.pensjon.kalkulator.ansatt.enhet

import no.nav.pensjon.kalkulator.ansatt.enhet.client.EnhetClient
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import org.springframework.stereotype.Service

/**
 * Tjeneste for å hente hvilke enheter den innloggede Nav-ansatte er tilknyttet.
 * "Enhet" betyr i denne sammenheng tjenestekontor, og enhets-ID er det samme som tjenestekontornummer (TKNR).
 */
@Service
class EnhetService(
    private val client: EnhetClient,
    private val ansattIdGetter: SecurityContextNavIdExtractor
) {
    fun tjenestekontorEnhetListe(): TjenestekontorEnheter =
        client.fetchTjenestekontorEnhetListe(ansattId = ansattIdGetter.id())
}
