package no.nav.pensjon.kalkulator.tech.security.egress.token

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.tech.security.egress.AuthType
import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.clientcred.ClientCredentialsEgressTokenService

class EgressAccessTokenFacadeTest : ShouldSpec({

    should("return access token for application when user type is application") {
        EgressAccessTokenFacade(
            clientCredentialsTokenService = arrangeToken(),
            jwtBearerTokenService = mockk(),
            tokenExchangeService = mockk(),
            azureAdOnBehalfOfTokenService = mockk()
        ).getAccessToken(
            authType = AuthType.MACHINE_INSIDE_NAV,
            audience = "audience1",
            ingressToken = null
        ).value shouldBe "token1"
    }
})

private fun arrangeToken(): ClientCredentialsEgressTokenService =
    mockk<ClientCredentialsEgressTokenService>().apply {
        every {
            getEgressToken(ingressToken = null, audience = "audience1", user = "")
        } returns RawJwt(value = "token1")
    }
