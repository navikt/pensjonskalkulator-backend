package no.nav.pensjon.kalkulator.tech.security.ingress

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.http.HttpHeaders
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils.hasLength
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
        val auth: String? = request?.getHeader(HttpHeaders.AUTHORIZATION)
        val pidHeaderProvided: Boolean = hasLength(request?.getHeader(CustomHttpHeaders.PID))

        log.error {
            "Authentication failed: ${exception?.message} - URI: ${request?.requestURI}" +
                    " - ${CustomHttpHeaders.PID} header: $pidHeaderProvided - claims: ${claimsAsString(auth)}"
        }

        entryPoint.commence(request, response, exception)
    }

    private companion object {
        private val decoder = Base64.getDecoder()
        private val mapper = jacksonObjectMapper()

        private fun claimsAsString(auth: String?): String {
            if (auth == null) return "(no auth)"

            val jwt = auth.substring("Bearer ".length)
            val payload = decoder.decode(jwt.split(".")[1])
            val claims: Map<String, Any?> = mapper.readValue(payload)
            return "iss ${claims["iss"]}; aud ${claims["aud"]}; iat ${claims["iat"]}; exp ${claims["exp"]}"
        }
    }
}
