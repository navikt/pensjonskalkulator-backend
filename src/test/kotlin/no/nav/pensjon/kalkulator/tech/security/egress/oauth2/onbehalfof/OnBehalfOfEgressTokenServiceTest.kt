package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.onbehalfof

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenData
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenDataGetter
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import java.time.LocalDateTime

class OnBehalfOfEgressTokenServiceTest : ShouldSpec({

    should("return token from OBO client") {
        OnBehalfOfEgressTokenService(
            tokenGetter = arrangeToken(id = "Z123456", token = "obo-token"),
            navIdGetter = arrangeLogin(id = "Z123456")
        ).getEgressToken(
            ingressToken = "ingress-jwt",
            audience = "dev-gcp:ns:app"
        ) shouldBe RawJwt("obo-token")
    }

    should("use 'unknown' when Nav-ident is empty") {
        OnBehalfOfEgressTokenService(
            tokenGetter = arrangeToken(id = "unknown", token = "obo-token-unknown"),
            navIdGetter = arrangeLogin(id = "") // Nav-ident is empty
        ).getEgressToken(
            ingressToken = "ingress-jwt",
            audience = "dev-gcp:ns:app"
        ) shouldBe RawJwt("obo-token-unknown")
    }

    should("throw when ingress token is null") {
        shouldThrow<IllegalArgumentException> {
            OnBehalfOfEgressTokenService(
                tokenGetter = mockk(),
                navIdGetter = mockk()
            ).getEgressToken(
                ingressToken = null,
                audience = "dev-gcp:ns:app"
            )
        }.message shouldBe "Missing ingressToken for OBO flow"
    }
})

private fun arrangeLogin(id: String): SecurityContextNavIdExtractor =
    mockk<SecurityContextNavIdExtractor>().apply { every { id() } returns id }

private fun arrangeToken(id: String, token: String): TokenDataGetter =
    mockk<TokenDataGetter>().apply {
        every {
            getTokenData(any(), any(), eq(id))
        } returns tokenData(accessToken = token)
    }

private fun tokenData(accessToken: String) =
    TokenData(
        accessToken,
        idToken = "",
        refreshToken = "",
        issuedTime = LocalDateTime.now(),
        expiresInSeconds = 3600
    )
