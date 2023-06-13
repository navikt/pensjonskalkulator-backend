package no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client

import no.nav.pensjon.kalkulator.tech.security.egress.token.unt.client.fssgw.dto.UsernameTokenDto

interface UsernameTokenClient {
    fun fetchUsernameToken(): UsernameTokenDto
}
