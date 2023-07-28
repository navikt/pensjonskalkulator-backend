package no.nav.pensjon.kalkulator.tech.security.egress.token.saml

import java.time.LocalDateTime

data class SamlTokenData(
    val assertion: String,
    val issuedTime: LocalDateTime,
    val expiresInSeconds: Long
)
