package no.nav.pensjon.kalkulator.tech.security.egress.oauth2

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.tech.security.egress.token.TokenData
import java.time.LocalDateTime

class OAuth2TokenDataMapperTest : ShouldSpec({

    should("map values") {
        val dto = OAuth2TokenDto().apply {
            setAccessToken("access-token")
            setIdToken("id-token")
            setRefreshToken("refresh-token")
            setExpiresIn(1)
        }

        val tokenData: TokenData = OAuth2TokenDataMapper.map(dto, LocalDateTime.MIN)

        with(tokenData) {
            accessToken shouldBe dto.getAccessToken()
            idToken shouldBe dto.getIdToken()
            refreshToken shouldBe dto.getRefreshToken()
            issuedTime shouldBe LocalDateTime.MIN
            expiresInSeconds shouldBe 1L
        }
    }

    should("allow null values for ID token and refresh token") {
        val dto = OAuth2TokenDto().apply {
            setIdToken(null)
            setRefreshToken(null)
            setAccessToken("access-token")
            setExpiresIn(1)
        }

        val tokenData: TokenData = OAuth2TokenDataMapper.map(dto, LocalDateTime.MIN)

        with(tokenData) {
            idToken shouldBe ""
            refreshToken shouldBe ""
        }
    }
})
