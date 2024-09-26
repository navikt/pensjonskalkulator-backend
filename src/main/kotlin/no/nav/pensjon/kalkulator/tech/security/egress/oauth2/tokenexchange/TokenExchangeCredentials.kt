package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.tokenexchange

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Credentials used when requesting a token exchange.
 */
@Component
class TokenExchangeCredentials(
    @Value("\${token.x.client.id}") val clientId: String,
    @Value("\${token.x.private.jwk}") val jwk: String
)
