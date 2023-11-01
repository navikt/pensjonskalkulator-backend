package no.nav.pensjon.kalkulator.tech.security.ingress

import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.failure
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult.success
import org.springframework.security.oauth2.jwt.Jwt

class AudienceValidator(val audience: String) : OAuth2TokenValidator<Jwt> {

    override fun validate(jwt: Jwt): OAuth2TokenValidatorResult =
        if (jwt.audience.contains(audience))
            success()
        else
            failure(OAuth2Error("Invalid audience: ${jwt.audience.joinToString()}"))
}
