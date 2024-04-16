package no.nav.pensjon.kalkulator.tech.security.ingress

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

/**
 * Authentication failure is normally a client error (and should thus not be logged as an error),
 * but if the server has a faulty validation logic, the failure is actually a server error.
 * For this reason the failure is logged as an error in any case.
 */
@Component
class LoggingAuthenticationEntryPoint : AuthenticationEntryPoint {

    private val log = KotlinLogging.logger {}
    private val entryPoint: AuthenticationEntryPoint = BearerTokenAuthenticationEntryPoint()

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        log.error { "Authentication failed: ${exception?.message}" }
        val auth: String? = (request as HttpServletRequest).getHeader(HttpHeaders.AUTHORIZATION)
        log.debug { auth ?: "(no Authorization header)" }
        entryPoint.commence(request, response, exception)
    }
}
