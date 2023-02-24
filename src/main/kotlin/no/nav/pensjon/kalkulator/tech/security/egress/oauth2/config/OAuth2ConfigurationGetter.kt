package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.config

interface OAuth2ConfigurationGetter {

    fun getIssuer(): String

    fun getAuthorizationEndpoint(): String

    fun getTokenEndpoint(): String

    fun getEndSessionEndpoint(): String

    fun getJsonWebKeySetUri(): String

    fun refresh()
}
