package no.nav.pensjon.kalkulator.tech.security.ingress

import no.nav.pensjon.kalkulator.person.Pid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class SecurityContextPidExtractor {

    fun pid(): Pid? = pidFromSecurityContext()?.let(::Pid)

    private companion object {

        private const val PID_CLAIM_KEY = "pid"

        private fun pidFromSecurityContext(): String? = jwt()?.claims?.get(PID_CLAIM_KEY) as? String

        private fun jwt(): Jwt? = SecurityContextHolder.getContext().authentication?.credentials as? Jwt
    }
}
