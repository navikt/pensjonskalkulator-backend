package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.config

import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.tech.web.WebClientConfig
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.spy
import org.springframework.http.HttpStatus
import java.util.function.Supplier

class OAuth2ConfigurationClientTest : WebClientTest() {

    private lateinit var configGetter: OAuth2ConfigurationGetter

    @BeforeEach
    fun initialize() {
        val webClient = spy(WebClientConfig().regularWebClient())
        configGetter = OAuth2ConfigurationClient(baseUrl(), webClient, "1")
    }

    @Test
    fun `getIssuer returns token endpoint and uses cache`() {
        testValueReturnAndCacheUse(configGetter::getIssuer)
    }

    @Test
    fun `getAuthorizationEndpoint returns authorization endpoint and uses cache`() {
        testValueReturnAndCacheUse(configGetter::getAuthorizationEndpoint)
    }

    @Test
    fun `getTokenEndpoint returns token endpoint and uses cache`() {
        testValueReturnAndCacheUse(configGetter::getTokenEndpoint)
    }

    @Test
    fun `getJsonWebKeySetUri returns JSON Web Key Set URI and uses cache`() {
        testValueReturnAndCacheUse(configGetter::getJsonWebKeySetUri)
    }

    private fun testValueReturnAndCacheUse(valueReturner: Supplier<String>) {
        arrange(configurationResponse(baseUrl()))
        val initialValue = valueReturner.get()
        arrange(configurationResponse(baseUrl(), "/new"))
        val cachedValue = valueReturner.get()
        assertEquals(cachedValue, initialValue)

        // Refresh will cause new web call in the get() that follows:
        configGetter.refresh()
        val freshValue = valueReturner.get()
        assertEquals("$initialValue/new", freshValue)
    }

    private fun configurationResponse(url: String): MockResponse = configurationResponse(url, "")

    private fun configurationResponse(url: String, modification: String): MockResponse {
        return jsonResponse(HttpStatus.OK)
            .setBody(
                """{
  "token_endpoint": "$url$modification",
  "token_endpoint_auth_methods_supported": ["client_secret_post", "private_key_jwt", "client_secret_basic"],
  "jwks_uri": "$url$modification",
  "response_modes_supported": ["query", "fragment", "form_post"],
  "subject_types_supported": ["pairwise"],
  "id_token_signing_alg_values_supported": ["RS256"],
  "response_types_supported": ["code", "id_token", "code id_token", "id_token token"],
  "scopes_supported": ["openid", "profile", "email", "offline_access"],
  "issuer": "www.issuer.org$modification",
  "request_uri_parameter_supported": false,
  "userinfo_endpoint": "$url",
  "authorization_endpoint": "$url$modification",
  "device_authorization_endpoint": "$url",
  "http_logout_supported": true,
  "frontchannel_logout_supported": true,
  "end_session_endpoint": "$url",
  "claims_supported": ["sub", "iss", "cloud_instance_name", "cloud_instance_host_name", "cloud_graph_host_name", "msgraph_host", "aud", "exp", "iat", "auth_time", "acr", "nonce", "preferred_username", "name", "tid", "ver", "at_hash", "c_hash", "email"],
  "tenant_region_scope": "EU",
  "cloud_instance_name": "microsoftonline.com",
  "cloud_graph_host_name": "graph.windows.net",
  "msgraph_host": "graph.microsoft.com",
  "rbac_url": "$url"
}"""
            )
    }
}
