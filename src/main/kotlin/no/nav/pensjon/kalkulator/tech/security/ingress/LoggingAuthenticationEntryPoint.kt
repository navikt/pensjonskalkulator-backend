package no.nav.pensjon.kalkulator.tech.security.ingress

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.util.*

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
        auth?.let(::logJwtClaims)
        log.debug { auth ?: "(no Authorization header)" }
        entryPoint.commence(request, response, exception)
    }

    private fun logJwtClaims(auth: String) {
        try {
            val jwt = auth.substring("Bearer ".length)
            val payload = decoder.decode(jwt.split(".")[1])
            val claims: Map<String, Any?> = mapper.readValue(payload)
            log.info { "Bad JWT - iss ${claims["iss"]}; aud ${claims["aud"]}; iat ${claims["iat"]}; exp ${claims["exp"]}" }
        } catch (e: Exception) {
            log.warn(e) { "Failed to log info about bad JWT - ${e.message}" }
        }
    }

    private companion object {
        private val decoder = Base64.getDecoder()
        private val mapper = jacksonObjectMapper()
    }
}
