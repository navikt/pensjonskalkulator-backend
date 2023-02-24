package no.nav.pensjon.kalkulator.tech.security.egress.token

interface TokenDataGetter {
    fun getTokenData(accessParameter: TokenAccessParameter, audience: String): TokenData
}
