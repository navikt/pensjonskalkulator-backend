package no.nav.pensjon.kalkulator.tech.security.egress.oauth2

import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*

enum class AuthorizationGrantCombo(val value: String, val parameterName: String) {

    CLIENT_CREDENTIALS(
        value = AuthorizationGrantType.CLIENT_CREDENTIALS.value,
        parameterName = SCOPE
    ),

    JWT_BEARER(
        value = AuthorizationGrantType.JWT_BEARER.value,
        parameterName = ASSERTION
    ),

    TOKEN_EXCHANGE(
        value = AuthorizationGrantType.TOKEN_EXCHANGE.value,
        parameterName = SUBJECT_TOKEN
    ),

    ON_BEHALF_OF(
        value = AuthorizationGrantType.JWT_BEARER.value,
        parameterName = ASSERTION
    )
}
