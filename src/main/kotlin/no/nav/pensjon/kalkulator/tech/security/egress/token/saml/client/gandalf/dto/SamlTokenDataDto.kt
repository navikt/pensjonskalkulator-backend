package no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf.dto

data class SamlTokenDataDto(
    val access_token: String,
    val issued_token_type: String,
    val token_type: String,
    val expires_in: Int
)
