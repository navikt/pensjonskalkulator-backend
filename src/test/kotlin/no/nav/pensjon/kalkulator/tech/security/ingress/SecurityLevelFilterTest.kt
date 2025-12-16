package no.nav.pensjon.kalkulator.tech.security.ingress

import io.kotest.core.spec.style.ShouldSpec
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.kalkulator.mock.MockAuthentication
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentertRolle
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import java.io.PrintWriter

class SecurityLevelFilterTest : ShouldSpec({

    context("strengt fortrolig adresse and insufficient security level") {
        should("break the filter chain") {
            arrangeSecurity(securityLevel = "idporten-loa-substantial", rolle = RepresentertRolle.SELV) // insufficient
            val writer = arrangeWriter()
            val response = arrangeForbiddenResponse(writer)
            val chain = mockk<FilterChain>()

            SecurityLevelFilter(
                adresseService = arrangeAdressebeskyttelse(gradering = AdressebeskyttelseGradering.STRENGT_FORTROLIG),
                pidGetter = mockk(relaxed = true)
            ).doFilter(request = arrangeRequest(), response, chain)

            verify { chain wasNot Called }
            verify(exactly = 1) { response.status = 403 }
            verify(exactly = 1) { response.contentType = MediaType.APPLICATION_JSON_VALUE }
            verify(exactly = 1) { writer.append("""{ "reason": "INSUFFICIENT_LEVEL_OF_ASSURANCE" }""") }
        }
    }

    context("strengt fortrolig adresse and high security level") {
        should("continue the filter chain") {
            arrangeSecurity(securityLevel = "idporten-loa-high", rolle = RepresentertRolle.SELV)
            val request = arrangeRequest()
            val response = arrangeResponse()
            val chain = arrangeFilterChain(request, response)

            SecurityLevelFilter(
                adresseService = arrangeAdressebeskyttelse(gradering = AdressebeskyttelseGradering.STRENGT_FORTROLIG),
                pidGetter = mockk()
            ).doFilter(request, response, chain)

            verify(exactly = 1) { chain.doFilter(request, response) }
        }
    }

    context("strengt fortrolig adresse and security level 4") {
        should("continue the filter chain") {
            arrangeSecurity(securityLevel = "Level4", rolle = RepresentertRolle.FULLMAKT_GIVER)
            val request = arrangeRequest()
            val response = arrangeResponse()
            val chain = arrangeFilterChain(request, response)

            SecurityLevelFilter(
                adresseService = arrangeAdressebeskyttelse(gradering = AdressebeskyttelseGradering.STRENGT_FORTROLIG),
                pidGetter = mockk()
            ).doFilter(request, response, chain)

            verify(exactly = 1) { chain.doFilter(request, response) }
        }
    }

    context("strengt fortrolig adresse and security level 3") {
        should("break the filter chain") {
            arrangeSecurity(securityLevel = "Level3", rolle = RepresentertRolle.FULLMAKT_GIVER)
            val writer = arrangeWriter()
            val response = arrangeForbiddenResponse(writer)
            val chain = mockk<FilterChain>()

            SecurityLevelFilter(
                adresseService = arrangeAdressebeskyttelse(gradering = AdressebeskyttelseGradering.STRENGT_FORTROLIG),
                pidGetter = mockk(relaxed = true)
            ).doFilter(request = arrangeRequest(), response, chain)

            verify { chain wasNot Called }
            verify(exactly = 1) { response.status = 403 }
            verify(exactly = 1) { response.contentType = MediaType.APPLICATION_JSON_VALUE }
            verify(exactly = 1) { writer.append("""{ "reason": "INSUFFICIENT_LEVEL_OF_ASSURANCE" }""") }
        }
    }

    context("fortrolig adresse and substantial security level") {
        should("continue the filter chain") {
            arrangeSecurity(securityLevel = "idporten-loa-substantial", rolle = RepresentertRolle.SELV)
            val request = arrangeRequest()
            val response = arrangeResponse()
            val chain = arrangeFilterChain(request, response)

            SecurityLevelFilter(
                adresseService = arrangeAdressebeskyttelse(gradering = AdressebeskyttelseGradering.FORTROLIG),
                pidGetter = mockk(relaxed = true)
            ).doFilter(request, response, chain)

            verify(exactly = 1) { chain.doFilter(request, response) }
        }
    }

    context("veileder innlogget") {
        should("continue the filter chain") {
            arrangeSecurity(securityLevel = "", rolle = RepresentertRolle.UNDER_VEILEDNING)
            val request = arrangeRequest()
            val response = arrangeResponse()
            val chain = arrangeFilterChain(request, response)

            SecurityLevelFilter(
                adresseService = arrangeAdressebeskyttelse(gradering = AdressebeskyttelseGradering.STRENGT_FORTROLIG),
                pidGetter = mockk()
            ).doFilter(request, response, chain)

            verify(exactly = 1) { chain.doFilter(request, response) }
        }
    }

    context("'feature' request") {
        should("skip the access check and continue the filter chain") {
            arrangeSecurity(securityLevel = "Level3", rolle = RepresentertRolle.SELV)
            val request = arrangeRequest("/api/feature/foo")
            val response = arrangeResponse()
            val chain = arrangeFilterChain(request, response)

            SecurityLevelFilter(
                adresseService = arrangeAdressebeskyttelse(gradering = AdressebeskyttelseGradering.STRENGT_FORTROLIG),
                pidGetter = mockk()
            ).doFilter(request, response, chain)

            verify(exactly = 0) { response.status = 403 } // i.e., access check is skipped
            verify(exactly = 1) { chain.doFilter(request, response) }
        }
    }
})

private fun arrangeFilterChain(request: HttpServletRequest, response: HttpServletResponse): FilterChain =
    mockk<FilterChain>().apply {
        every { doFilter(request, response) } returns Unit
    }

private fun arrangeAdressebeskyttelse(gradering: AdressebeskyttelseGradering): FortroligAdresseService =
    mockk<FortroligAdresseService>().apply {
        every { adressebeskyttelseGradering(any()) } returns gradering
    }

private fun arrangeRequest(uri: String = "/api/foo"): HttpServletRequest =
    mockk<HttpServletRequest>().apply {
        every { requestURI } returns uri
    }

private fun arrangeResponse(printWriter: PrintWriter = mockk()): HttpServletResponse =
    mockk<HttpServletResponse>().apply {
        every { writer } returns printWriter
    }

private fun arrangeForbiddenResponse(printWriter: PrintWriter): HttpServletResponse =
    arrangeResponse(printWriter).apply {
        every { status = 403 } returns Unit
        every { contentType = "application/json" } returns Unit
    }

private fun arrangeWriter(): PrintWriter =
    mockk<PrintWriter>().apply {
        every { append("""{ "reason": "INSUFFICIENT_LEVEL_OF_ASSURANCE" }""") } returns this
    }

private fun arrangeSecurity(securityLevel: String, rolle: RepresentertRolle) {
    SecurityContextHolder.setContext(
        SecurityContextImpl(
            EnrichedAuthentication(
                initialAuth = MockAuthentication(claimKey = "acr", claimValue = securityLevel),
                egressTokenSuppliersByService = EgressTokenSuppliersByService(emptyMap()),
                target = RepresentasjonTarget(pid = null, rolle)
            )
        )
    )
}
