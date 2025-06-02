package no.nav.pensjon.kalkulator.testutil

import no.nav.pensjon.kalkulator.WebClientTestConfig
import no.nav.pensjon.kalkulator.mock.TestObjects.jwt
import no.nav.pensjon.kalkulator.mock.TestObjects.pid1
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentertRolle
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

object Arrange {
    fun security() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext())

        SecurityContextHolder.getContext().authentication = EnrichedAuthentication(
            initialAuth = TestingAuthenticationToken("TEST_USER", jwt),
            egressTokenSuppliersByService = EgressTokenSuppliersByService(mapOf()),
            target = RepresentasjonTarget(pid = pid1, rolle = RepresentertRolle.SELV)
        )
    }

    fun webClientContextRunner(): ApplicationContextRunner =
        ApplicationContextRunner().withConfiguration(
            AutoConfigurations.of(WebClientTestConfig::class.java)
        )
}

fun MockWebServer.arrangeOkJsonResponse(body: String) {
    this.enqueue(
        MockResponse()
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setResponseCode(HttpStatus.OK.value()).setBody(body)
    )
}

fun MockWebServer.arrangeResponse(status: HttpStatus, body: String) {
    this.enqueue(MockResponse().setResponseCode(status.value()).setBody(body))
}
