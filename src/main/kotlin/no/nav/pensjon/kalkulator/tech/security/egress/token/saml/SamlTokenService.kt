package no.nav.pensjon.kalkulator.tech.security.egress.token.saml

import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.SamlTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf.map.SamlTokenDataMapper
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import org.springframework.stereotype.Service

@Service
class SamlTokenService(
    private val tokenClient: SamlTokenClient,
    private val expirationChecker: ExpirationChecker
) {
    private var tokenData: SamlTokenData? = null

    fun assertion() = tokenData().assertion

    private fun tokenData() =
        if (isCachedTokenValid())
            tokenData!!
        else
            freshTokenData().also { tokenData = it }

    private fun freshTokenData() =
        SamlTokenDataMapper.map(tokenClient.fetchSamlToken(), expirationChecker.time())

    private fun isCachedTokenValid() =
        tokenData?.let { !expirationChecker.isExpired(it.issuedTime, it.expiresInSeconds) } ?: false
}
