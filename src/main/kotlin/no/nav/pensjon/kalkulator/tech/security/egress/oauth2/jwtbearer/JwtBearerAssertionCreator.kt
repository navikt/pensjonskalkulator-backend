package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.jwtbearer

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class JwtBearerAssertionCreator(
    private val assertionBasis: JwtBearerAssertionBasis
) {
    private var cachedHeader: JWSHeader? = null
    private var cachedRsaKey: RSAKey? = null
    private var cachedSigner: RSASSASigner? = null

    fun assertion(scope: String): String =
        SignedJWT(header(), buildClaims(scope))
            .apply { sign(signer()) }
            .serialize()

    private fun buildHeader(): JWSHeader =
        JWSHeader.Builder(JWSAlgorithm.RS256)
            .keyID(rsaKey().keyID)
            .type(JOSEObjectType.JWT)
            .build()

    private fun buildClaims(scope: String): JWTClaimsSet =
        JWTClaimsSet.Builder()
            .issuer(assertionBasis.clientId)
            .audience(assertionBasis.issuer)
            .issueTime(now())
            .claim(CLAIM_NAME, scope)
            .expirationTime(expiration())
            .jwtID(jwtId())
            .build()

    private fun header(): JWSHeader = cachedHeader ?: buildHeader().also { cachedHeader = it }

    private fun rsaKey(): RSAKey = cachedRsaKey ?: RSAKey.parse(assertionBasis.clientJwk).also { cachedRsaKey = it }

    private fun signer(): RSASSASigner = cachedSigner ?: RSASSASigner(rsaKey().toPrivateKey()).also { cachedSigner = it }

    private companion object {
        private const val CLAIM_NAME = "scope"

        private fun jwtId(): String = UUID.randomUUID().toString()

        private fun now(): Date = Date.from(Instant.now())

        private fun expiration(): Date = Date.from(Instant.now().plusSeconds(100))
    }
}
