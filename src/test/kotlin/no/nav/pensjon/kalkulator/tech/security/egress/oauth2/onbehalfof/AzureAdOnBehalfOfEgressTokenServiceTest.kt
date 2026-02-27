package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.onbehalfof

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenData
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import java.time.LocalDateTime

class AzureAdOnBehalfOfEgressTokenServiceTest : FunSpec({

    test("getEgressToken returns token from OBO client") {
        val client = mockk<AzureAdOnBehalfOfClient>()
        val navIdExtractor = mockk<SecurityContextNavIdExtractor>()

        every { navIdExtractor.id() } returns "Z123456"
        every { client.getTokenData(any(), any(), eq("Z123456")) } returns TokenData(
            accessToken = "obo-token",
            idToken = "",
            refreshToken = "",
            issuedTime = LocalDateTime.now(),
            expiresInSeconds = 3600
        )

        val service = AzureAdOnBehalfOfEgressTokenService(client, navIdExtractor)
        val result = service.getEgressToken("ingress-jwt", "dev-gcp:ns:app", "")

        result shouldBe RawJwt("obo-token")
    }

    test("getEgressToken throws when ingressToken is null") {
        val client = mockk<AzureAdOnBehalfOfClient>()
        val navIdExtractor = mockk<SecurityContextNavIdExtractor>()

        val service = AzureAdOnBehalfOfEgressTokenService(client, navIdExtractor)

        shouldThrow<IllegalArgumentException> {
            service.getEgressToken(null, "dev-gcp:ns:app", "")
        }
    }

    test("getEgressToken uses 'unknown' when NAVident is empty") {
        val client = mockk<AzureAdOnBehalfOfClient>()
        val navIdExtractor = mockk<SecurityContextNavIdExtractor>()

        every { navIdExtractor.id() } returns ""
        every { client.getTokenData(any(), any(), eq("unknown")) } returns TokenData(
            accessToken = "obo-token-unknown",
            idToken = "",
            refreshToken = "",
            issuedTime = LocalDateTime.now(),
            expiresInSeconds = 3600
        )

        val service = AzureAdOnBehalfOfEgressTokenService(client, navIdExtractor)
        val result = service.getEgressToken("ingress-jwt", "dev-gcp:ns:app", "")

        result shouldBe RawJwt("obo-token-unknown")
    }
})
