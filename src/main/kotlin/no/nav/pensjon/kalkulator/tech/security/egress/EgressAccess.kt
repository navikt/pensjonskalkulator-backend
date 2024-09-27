package no.nav.pensjon.kalkulator.tech.security.egress

import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

object EgressAccess {

    fun token(service: EgressService): RawJwt =
        SecurityContextHolder.getContext().authentication.enriched().getEgressAccessToken(
            service,
            ingressToken = (SecurityContextHolder.getContext().authentication?.credentials as? Jwt)?.tokenValue
        )
}
