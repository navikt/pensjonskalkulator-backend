package no.nav.pensjon.kalkulator.tech.security.ingress

import no.nav.pensjon.kalkulator.person.Pid
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

class SecurityContextPidExtractor : PidGetter {

    override fun pid() = Pid(getPidFromSecurityContext())

    private companion object {

        private const val PID_CLAIM_KEY = "pid"

        private fun getPidFromSecurityContext() = extractPid(SecurityContextHolder.getContext().authentication)

        private fun extractPid(authentication: Authentication) = jwt(authentication).claims[PID_CLAIM_KEY] as String

        private fun jwt(authentication: Authentication) = authentication.credentials as Jwt
    }
}
