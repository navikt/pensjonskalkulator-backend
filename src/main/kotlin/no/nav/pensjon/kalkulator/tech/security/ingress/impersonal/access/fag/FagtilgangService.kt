package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.fag

import no.nav.pensjon.kalkulator.tech.security.ingress.jwt.SecurityContextClaimExtractor
import org.springframework.stereotype.Service

/**
 * Sjekker om den ansatte er medlem av en faggruppe som har tilgang til applikasjonen.
 * Gruppene angis i Nais-konfigurasjonen, og de vil finnes i OBO-tokenet dersom den ansatte er medlem.
 * OBO = On-behalf-of (fra Entra ID)
 */
@Service
class FagtilgangService {

    fun tilgangInnvilget(): Boolean =
        groupsFromSecurityContext().orEmpty().isNotEmpty()

    private companion object {
        private const val CLAIM_KEY = "groups"

        private fun groupsFromSecurityContext() =
            SecurityContextClaimExtractor.claim(CLAIM_KEY) as? List<*>
    }
}