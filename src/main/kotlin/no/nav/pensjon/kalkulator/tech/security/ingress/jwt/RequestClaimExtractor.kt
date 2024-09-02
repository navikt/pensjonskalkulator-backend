package no.nav.pensjon.kalkulator.tech.security.ingress.jwt

import com.nimbusds.jose.util.JSONObjectUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Component
class RequestClaimExtractor {

    fun extractAuthorizationClaim(request: HttpServletRequest, claimName: String) =
        request.getHeader(HttpHeaders.AUTHORIZATION)?.let { extractClaim(it, claimName) }

    private companion object {
        private const val JWT_PART_COUNT = 3
        private const val JWT_PAYLOAD_PART_INDEX = 1
        private const val JWT_PART_DELIMITER = "."

        @OptIn(ExperimentalEncodingApi::class)
        private val base64 = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)

        private fun extractClaim(jwt: String, claimName: String): String? {
            val parts = jwt.split(JWT_PART_DELIMITER)

            val payload: String? =
                if (parts.size == JWT_PART_COUNT)
                    parts[JWT_PAYLOAD_PART_INDEX]
                else
                    null

            return payload?.let(::toJson)?.get(claimName) as? String
        }

        @OptIn(ExperimentalEncodingApi::class)
        private fun toJson(value: String): MutableMap<String, Any>? =
            JSONObjectUtils.parse(String(base64.decode(value)))
    }
}
