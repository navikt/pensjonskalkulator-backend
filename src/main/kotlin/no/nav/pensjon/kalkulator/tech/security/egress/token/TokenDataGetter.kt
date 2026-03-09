package no.nav.pensjon.kalkulator.tech.security.egress.token

interface TokenDataGetter {
    fun getTokenData(accessParameter: TokenAccessParameter, scope: String, user: String): TokenData
}
