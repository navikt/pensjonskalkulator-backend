package no.nav.pensjon.kalkulator.tech.security.egress.oauth2

import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.OAuth2ParameterNames.CLIENT_ID
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.OAuth2ParameterNames.CLIENT_SECRET
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.OAuth2ParameterNames.GRANT_TYPE
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

class OAuth2ParameterBuilder {

    private lateinit var accessParameter: TokenAccessParameter
    private lateinit var clientId: String
    private lateinit var clientSecret: String

    fun tokenAccessParameter(value: TokenAccessParameter): OAuth2ParameterBuilder {
        accessParameter = value
        return this
    }

    fun clientId(value: String): OAuth2ParameterBuilder {
        clientId = value
        return this
    }

    fun clientSecret(value: String): OAuth2ParameterBuilder {
        clientSecret = value
        return this
    }

    fun buildClientCredentialsTokenRequestMap(): MultiValueMap<String, String> {
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add(GRANT_TYPE, accessParameter.getGrantTypeName())
        map.add(accessParameter.getParameterName(), accessParameter.value)
        map.add(CLIENT_ID, clientId)
        map.add(CLIENT_SECRET, clientSecret)
        return map
    }
}
