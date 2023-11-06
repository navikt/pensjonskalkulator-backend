package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group

import no.nav.pensjon.kalkulator.tech.security.ingress.jwt.SecurityContextClaimExtractor
import org.springframework.stereotype.Service

@Service
class SecurityContextGroupService : GroupService {

    override fun groups(): List<String> = groupsFromSecurityContext()?.map { it.toString() }.orEmpty()

    private companion object {
        private const val CLAIM_KEY = "groups"

        private fun groupsFromSecurityContext() = SecurityContextClaimExtractor.claim(CLAIM_KEY) as? List<*>
    }
}
