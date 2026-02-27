package no.nav.pensjon.kalkulator.tech.security.egress.oauth2

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenAccessParameter
import org.springframework.util.MultiValueMap

class OAuth2ParameterBuilderTest : ShouldSpec({

    should("build map with client credentials parameters") {
        val map = OAuth2ParameterBuilder()
            .clientId("id1")
            .clientSecret("secret1")
            .tokenAccessParameter(TokenAccessParameter.clientCredentials("scope1"))
            .buildClientCredentialsTokenRequestMap()

        assertMapValue("client_credentials", map, "grant_type")
        assertMapValue("scope1", map, "scope")
        assertMapValue("id1", map, "client_id")
        assertMapValue("secret1", map, "client_secret")
    }
})

private fun assertMapValue(expectedValue: String, map: MultiValueMap<String, String>, key: String) {
    map[key]!![0] shouldBe expectedValue
}