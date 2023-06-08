package no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client

import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf.dto.SamlTokenDataDto

interface SamlTokenClient {
    fun fetchSamlToken(): SamlTokenDataDto
}
