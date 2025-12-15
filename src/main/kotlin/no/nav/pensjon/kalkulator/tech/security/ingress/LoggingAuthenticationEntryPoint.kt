package no.nav.pensjon.kalkulator.tech.security.ingress

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.security.ingress.jwt.RequestClaimExtractor
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils.hasLength

/**
 * Authentication failure is normally a client error (and should thus not be logged as an error),
 * but if the server has a faulty validation logic, the failure is actually a server error.
 * For this reason the failure is logged as an error in any case.
 */
@Component
class LoggingAuthenticationEntryPoint(
    private val claimExtractor: RequestClaimExtractor
) : AuthenticationEntryPoint {

    private val log = KotlinLogging.logger {}
    private val entryPoint: AuthenticationEntryPoint = BearerTokenAuthenticationEntryPoint()

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val pidHeaderProvided: Boolean = hasLength(request.getHeader(CustomHttpHeaders.PID))
        val claims = claimExtractor.extractAuthorizationClaims(request)?.let(::claimsAsString)

        log.error {
            "Authentication failed: ${exception.message} - URI: ${request.requestURI}" +
                    " - ${CustomHttpHeaders.PID} header: $pidHeaderProvided - claims: $claims"
        }

        entryPoint.commence(request, response, exception)
    }

    private companion object {
        private fun claimsAsString(claims: Map<String, Any?>): String =
            "iss ${claims["iss"]}; aud ${claims["aud"]}; iat ${claims["iat"]}; exp ${claims["exp"]}"
    }
}
