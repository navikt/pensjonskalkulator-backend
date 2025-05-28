package no.nav.pensjon.kalkulator.tech.security.ingress

import mu.KotlinLogging
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success
import org.springframework.security.oauth2.jwt.Jwt

class AudienceValidator(val thisAppAudience: String) : OAuth2TokenValidator<Jwt> {

    private val log = KotlinLogging.logger {}

    override fun validate(jwt: Jwt): OAuth2TokenValidatorResult = validate(jwt.audience)

    private fun validate(audiences: List<String>): OAuth2TokenValidatorResult =
        if (audiences.contains(thisAppAudience))
            success()
        else
            "Invalid audience claim: ${audiences.joinToString()}".let {
                log.warn { it }
                failure(OAuth2Error(it))
            }
}
