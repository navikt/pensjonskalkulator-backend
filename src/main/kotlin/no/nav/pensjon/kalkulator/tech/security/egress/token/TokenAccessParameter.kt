package no.nav.pensjon.kalkulator.tech.security.egress.token

import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.AuthorizationGrantCombo

class TokenAccessParameter(private val type: AuthorizationGrantCombo, val value: String) {

    fun getGrantTypeName(): String = type.value

    fun getParameterName(): String = type.parameterName

    companion object {

        fun clientCredentials(scope: String) =
            TokenAccessParameter(AuthorizationGrantCombo.CLIENT_CREDENTIALS, scope)

        fun jwtBearer(assertion: String) =
            TokenAccessParameter(AuthorizationGrantCombo.JWT_BEARER, assertion)

        fun tokenExchange(subjectToken: String) =
            TokenAccessParameter(AuthorizationGrantCombo.TOKEN_EXCHANGE, subjectToken)
    }
}
