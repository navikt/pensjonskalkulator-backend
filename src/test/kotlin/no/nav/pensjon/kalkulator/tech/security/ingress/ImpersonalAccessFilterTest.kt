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
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.fag.FagtilgangService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.AvvisningAarsak
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.PopulasjonstilgangService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.TilgangResult

class ImpersonalAccessFilterTest : ShouldSpec({

    should("continue filter chain when no fødselsnummer (fnr) in header") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = null, uri = "/api/foo")
        val response = mockk<ServletResponse>()

        ImpersonalAccessFilter(
            pidGetter = mockk(),
            navIdExtractor = mockk(),
            fagtilgangService = mockk(),
            populasjonstilgangService = mockk(),
            auditor = mockk(),
        ).doFilter(request, response, chain)

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
            fagtilgangService = mockk(),
            populasjonstilgangService = mockk(),
            auditor = mockk(),
        ).doFilter(request, response, chain)

        verify(exactly = 0) { pidExtractor.pid() }
        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    should("report 'forbidden' and break filter chain when 'fagtilgang avvist'") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/api/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            navIdExtractor = arrangeNavIdExtractor(),
            fagtilgangService = arrangeFagtilgang(innvilget = false),
            populasjonstilgangService = arrangePopulasjonstilgang(innvilget()),
            auditor = mockk(),
        ).doFilter(request, response, chain)

        verify(exactly = 1) { response.sendError(403, "Tilgang nektet pga. manglende faggruppemedlemskap") }
        verify(exactly = 0) { chain.doFilter(request, response) }
    }

    should("report 'forbidden' and break filter chain when 'populasjonstilgang avvist'") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/api/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            navIdExtractor = arrangeNavIdExtractor(),
            fagtilgangService = arrangeFagtilgang(innvilget = true),
            populasjonstilgangService = arrangePopulasjonstilgang(result = avvist()),
            auditor = mockk(),
        ).doFilter(request, response, chain)

        verify(exactly = 1) { response.sendError(403, "Tilgang nektet pga. GEOGRAFISK: some reason") }
        verify(exactly = 0) { chain.doFilter(request, response) }
    }

    should("report 'forbidden' and break filter chain when 'tilgangssjekk feilet'") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/api/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePidError(),
            navIdExtractor = arrangeNavIdExtractor(),
            fagtilgangService = arrangeFagtilgang(innvilget = true),
            populasjonstilgangService = arrangePopulasjonstilgang(innvilget()),
            auditor = mockk(),
        ).doFilter(request, response, chain)

        verify(exactly = 1) {
            response.sendError(
                403,
                "Tilgang nektet pga. feil i tilgangssjekk - se logg for detaljer"
            )
        }
        verify(exactly = 0) { chain.doFilter(request, response) }
    }

    should("log audit info and continue filter chain when 'fagtilgang og populasjonstilgang innvilget'") {
        val chain = mockk<FilterChain>(relaxed = true)
        val auditor = mockk<Auditor>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            navIdExtractor = arrangeNavIdExtractor(),
            fagtilgangService = arrangeFagtilgang(innvilget = true),
            populasjonstilgangService = arrangePopulasjonstilgang(result = innvilget()),
            auditor = auditor,
        ).doFilter(request, response, chain)

        verify(exactly = 1) { auditor.audit(onBehalfOfPid = pid, requestUri = "/foo") }
        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    should("interrupt filter chain when 'populasjonstilgangssjekk feiler'") {
        val chain = mockk<FilterChain>(relaxed = true)
        val auditor = mockk<Auditor>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            navIdExtractor = arrangeNavIdExtractor(),
            fagtilgangService = arrangeFagtilgang(innvilget = true),
            populasjonstilgangService = arrangePopulasjonstilgangError(),
            auditor = auditor,
        ).doFilter(request, response, chain)

        verify(exactly = 0) { auditor.audit(onBehalfOfPid = pid, requestUri = "/foo") }
        verify(exactly = 0) { chain.doFilter(request, response) }
    }
})

private fun arrangePid(): PidExtractor =
    mockk<PidExtractor>().apply {
        every { pid() } returns pid
    }

private fun arrangePidError(): PidExtractor =
    mockk<PidExtractor>().apply {
        every { pid() } throws RuntimeException("feil")
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

private fun arrangeFagtilgang(innvilget: Boolean): FagtilgangService =
    mockk<FagtilgangService>().apply {
        every { tilgangInnvilget() } returns innvilget
    }

private fun arrangePopulasjonstilgang(result: TilgangResult): PopulasjonstilgangService =
    mockk<PopulasjonstilgangService>().apply {
        every { sjekkTilgang(pid) } returns result
    }

private fun arrangePopulasjonstilgangError(): PopulasjonstilgangService =
    mockk<PopulasjonstilgangService>().apply {
        every { sjekkTilgang(pid) } returns feil()
    }

private fun innvilget() =
    TilgangResult(
        innvilget = true,
        avvisningAarsak = null,
        begrunnelse = null,
        traceId = null
    )

private fun avvist() =
    TilgangResult(
        innvilget = false,
        avvisningAarsak = AvvisningAarsak.GEOGRAFISK,
        begrunnelse = "some reason",
        traceId = null
    )

private fun feil() =
    TilgangResult(
        innvilget = false,
        avvisningAarsak = AvvisningAarsak.POPULASJONSTILGANGSSJEKK_FEILET,
        begrunnelse = "feil",
        traceId = null
    )
