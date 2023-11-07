package no.nav.pensjon.kalkulator.tech.security.ingress

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.jwt.SecurityContextClaimExtractor
import org.springframework.stereotype.Component

@Component
class SecurityContextPidExtractor {

    fun pid(): Pid? = pidFromSecurityContext()?.let(::Pid)

    private companion object {
        private const val CLAIM_KEY = "pid"

        private fun pidFromSecurityContext(): String? = SecurityContextClaimExtractor.claim(CLAIM_KEY) as? String
    }
}
