package no.nav.pensjon.kalkulator.tech.security.ingress.jwt

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.json.JsonMapper
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Component
class RequestClaimExtractor(private val mapper: JsonMapper) {

    fun extractAuthorizationClaims(request: HttpServletRequest): Map<String, Any?>? =
        request.getHeader(HttpHeaders.AUTHORIZATION)?.let(::extractClaims)

    fun extractAuthorizationClaim(request: HttpServletRequest, claimName: String): String? =
        extractAuthorizationClaims(request)?.get(claimName) as? String

    private fun extractClaims(jwt: String): Map<String, Any?>? {
        val parts = jwt.split(JWT_PART_DELIMITER)

        val payload: String? =
            if (parts.size == JWT_PART_COUNT)
                parts[JWT_PAYLOAD_PART_INDEX]
            else
                null

        return payload?.let(::claims)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun claims(encodedValue: String): Map<String, Any?> {
        val bytes = base64.decode(encodedValue)
        return mapper.readValue(bytes, object : TypeReference<Map<String, Any?>>() {})
    }

    private companion object {
        private const val JWT_PART_COUNT = 3
        private const val JWT_PAYLOAD_PART_INDEX = 1
        private const val JWT_PART_DELIMITER = "."

        @OptIn(ExperimentalEncodingApi::class)
        private val base64 = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)
    }
}
