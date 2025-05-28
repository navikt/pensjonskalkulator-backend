package no.nav.pensjon.kalkulator.tech.security

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.jwt.RequestClaimExtractor
import org.springframework.security.authentication.ProviderManager

class SecurityConfigurationTest : FunSpec({

    val claimExtractor = mockk<RequestClaimExtractor>()
    val personalProviderManager = mockk<ProviderManager>()
    val impersonalProviderManager = mockk<ProviderManager>()
    val universalProviderManager = mockk<ProviderManager>()

    test("tokenAuthenticationManagerResolver uses universalProviderManager when request for feature toggle") {
        val request = mockk<HttpServletRequest>().also {
            every { it.requestURI } returns "/api/feature/"
        }

        SecurityConfiguration(claimExtractor).tokenAuthenticationManagerResolver(
            personalProviderManager,
            impersonalProviderManager,
            universalProviderManager
        ).resolve(request) shouldBe universalProviderManager
    }

    test("tokenAuthenticationManagerResolver uses universalProviderManager when request for encryption") {
        val request = mockk<HttpServletRequest>().also {
            every { it.requestURI } returns "/api/v1/encrypt"
            every { it.getHeader("fnr") } returns pid.value // universalProviderManager used despite this header
        }

        SecurityConfiguration(claimExtractor).tokenAuthenticationManagerResolver(
            personalProviderManager,
            impersonalProviderManager,
            universalProviderManager
        ).resolve(request) shouldBe universalProviderManager
    }

    test("tokenAuthenticationManagerResolver uses impersonalProviderManager when request has fnr header") {
        val request = mockk<HttpServletRequest>().also {
            every { it.getHeader("fnr") } returns pid.value
            every { it.requestURI } returns "non-universal"
        }

        SecurityConfiguration(claimExtractor).tokenAuthenticationManagerResolver(
            personalProviderManager,
            impersonalProviderManager,
            universalProviderManager
        ).resolve(request) shouldBe impersonalProviderManager
    }

    test("tokenAuthenticationManagerResolver uses impersonalProviderManager when request for ansatt-ID has NAVident claim") {
        val request = mockk<HttpServletRequest>().also {
            every { it.requestURI } returns "/api/v1/ansatt-id"
        }
        val navIdentClaimExtractor = mockk<RequestClaimExtractor>().also {
            every { it.extractAuthorizationClaim(request, "NAVident") } returns "non-empty"
        }

        SecurityConfiguration(navIdentClaimExtractor).tokenAuthenticationManagerResolver(
            personalProviderManager,
            impersonalProviderManager,
            universalProviderManager
        ).resolve(request) shouldBe impersonalProviderManager
    }

    test("tokenAuthenticationManagerResolver uses personalProviderManager by default") {
        val request = mockk<HttpServletRequest>().also {
            every { it.getHeader("fnr") } returns null
            every { it.requestURI } returns "foo"
        }

        SecurityConfiguration(claimExtractor).tokenAuthenticationManagerResolver(
            personalProviderManager,
            impersonalProviderManager,
            universalProviderManager
        ).resolve(request) shouldBe personalProviderManager
    }
})
