package no.nav.pensjon.kalkulator.tech.security.ingress

import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.ImpersonalAccessFilter
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.TilgangService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client.AvvisningAarsak
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client.TilgangResult

class ImpersonalAccessFilterTest : ShouldSpec({

    should("continue filter chain when no fnr in header") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = null, uri = "/api/foo")
        val response = mockk<ServletResponse>()

        ImpersonalAccessFilter(
            pidGetter = mockk(),
            navIdExtractor = mockk(),
            tilgangService = mockk(),
            auditor = mockk(),
        ).doFilter(request, response, chain)

        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    should("report 'forbidden' and break filter chain when tilgangService avviser") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/api/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            navIdExtractor = arrangeNavIdExtractor(),
            tilgangService = arrangeTilgangService(avvist()),
            auditor = mockk(),
        ).doFilter(request, response, chain)

        verify(exactly = 1) { response.sendError(403, "Adgang nektet pga. ${AvvisningAarsak.GEOGRAFISK}:some reason") }
        verify(exactly = 0) { chain.doFilter(request, response) }
    }

    should("log audit info and continue filter chain when tilgangService innvilger") {
        val chain = mockk<FilterChain>(relaxed = true)
        val auditor = mockk<Auditor>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            navIdExtractor = arrangeNavIdExtractor(),
            tilgangService = arrangeTilgangService(innvilget()),
            auditor = auditor,
        ).doFilter(request, response, chain)

        verify(exactly = 1) { auditor.audit(pid, "/foo") }
        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    should("skip access check and continue filter chain if 'feature' request") {
        val response = mockk<HttpServletResponse>()
        val request = arrangeRequest(pid = pid.value, uri = "/api/feature/foo")
        val pidExtractor = mockk<PidExtractor>()
        val chain = mockk<FilterChain>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = pidExtractor,
            navIdExtractor = mockk(),
            tilgangService = mockk(),
            auditor = mockk(),
        ).doFilter(request, response, chain)

        verify(exactly = 0) { pidExtractor.pid() }
        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    should("continue filter chain and log warning when tilgangService throws exception") {
        val chain = mockk<FilterChain>(relaxed = true)
        val auditor = mockk<Auditor>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            navIdExtractor = arrangeNavIdExtractor(),
            tilgangService = arrangeTilgangServiceFailing(),
            auditor = auditor,
        ).doFilter(request, response, chain)

        verify(exactly = 1) { auditor.audit(pid, "/foo") }
        verify(exactly = 1) { chain.doFilter(request, response) }
    }
})

private fun arrangePid(): PidExtractor =
    mockk<PidExtractor>().apply {
        every { pid() } returns pid
    }

private fun arrangeNavIdExtractor(): SecurityContextNavIdExtractor =
    mockk<SecurityContextNavIdExtractor>().apply {
        every { id() } returns "Z123456"
    }

private fun arrangeRequest(pid: String?, uri: String): HttpServletRequest =
    mockk<HttpServletRequest>().apply {
        every { getHeader("fnr") } returns pid
        every { requestURI } returns uri
    }

private fun arrangeTilgangService(result: TilgangResult): TilgangService =
    mockk<TilgangService>().apply {
        every { sjekkTilgang(pid) } returns result
    }

private fun arrangeTilgangServiceFailing(): TilgangService =
    mockk<TilgangService>().apply {
        every { sjekkTilgang(pid) } throws RuntimeException("connection failed")
    }

private fun innvilget() = TilgangResult(
    innvilget = true,
    avvisningAarsak = null,
    begrunnelse = null,
    traceId = null
)

private fun avvist() = TilgangResult(
    innvilget = false,
    avvisningAarsak = AvvisningAarsak.GEOGRAFISK,
    begrunnelse = "some reason",
    traceId = null
)
