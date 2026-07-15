package no.nav.pensjon.kalkulator.tech.security.ingress

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant

class AudienceValidatorTest : ShouldSpec({

    context("valid JWT audience") {
        should("give result with no errors") {
            AudienceValidator(thisAppAudience = "good")
                .validate(jwt(audience = "good")).errors shouldHaveSize 0
        }
    }

    context("invalid JWT audience") {
        should("give descriptive error code") {
            val result = AudienceValidator(thisAppAudience = "good")
                .validate(jwt(audience = "bad")).errors as ArrayList<*>

            result shouldHaveSize 1
            (result[0] as OAuth2Error).errorCode shouldBe "Invalid audience claim: bad"
        }
    }
})

private fun jwt(audience: String) =
    Jwt(
        "jwt",
        Instant.ofEpochSecond(1),
        Instant.ofEpochSecond(2),
        mapOf("k" to "v"),
        mapOf("aud" to audience)
    )