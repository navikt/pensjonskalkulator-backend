package no.nav.pensjon.kalkulator.tech.security.ingress

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jakarta.servlet.DispatcherType
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.io.PrintWriter
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.ImpersonalAccessFilter
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.TilgangsnektResponse
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.fag.FagtilgangService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.AvvisningAarsak
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.CacheAwarePopulasjonstilgangService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.TilgangResult
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import tools.jackson.databind.ObjectMapper

private val objectMapper = ObjectMapper()

class ImpersonalAccessFilterTest : ShouldSpec({

    should("continue filter chain when no fødselsnummer (fnr) in header") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = null, uri = "/api/foo")
        val response = mockk<ServletResponse>()

        ImpersonalAccessFilter(
            pidGetter = mockk(),
            fagtilgangService = mockk(),
            populasjonstilgangService = mockk(),
            auditor = mockk(),
            objectMapper = objectMapper,
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
            fagtilgangService = mockk(),
            populasjonstilgangService = mockk(),
            auditor = mockk(),
            objectMapper = objectMapper,
        ).doFilter(request, response, chain)

        verify(exactly = 0) { pidExtractor.pid() }
        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    should("report 'forbidden' and break filter chain when 'fagtilgang avvist'") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/api/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)
        val writer = mockk<PrintWriter>(relaxed = true)
        every { response.writer } returns writer

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            fagtilgangService = arrangeFagtilgang(innvilget = false),
            populasjonstilgangService = arrangePopulasjonstilgang(result = null),
            auditor = mockk(),
            objectMapper = objectMapper,
        ).doFilter(request, response, chain)

        verify(exactly = 1) { response.status = 403 }
        verifyTilgangsnektBody(writer, forventetDetail = "manglende faggruppemedlemskap")
        verify(exactly = 0) { chain.doFilter(request, response) }
    }

    should("report 'forbidden' and break filter chain when 'populasjonstilgang avvist'") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/api/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)
        val writer = mockk<PrintWriter>(relaxed = true)
        every { response.writer } returns writer

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            fagtilgangService = arrangeFagtilgang(innvilget = true),
            populasjonstilgangService = arrangePopulasjonstilgang(result = avvist),
            auditor = mockk(),
            objectMapper = objectMapper,
        ).doFilter(request, response, chain)

        verify(exactly = 1) { response.status = 403 }
        verifyTilgangsnektBody(writer, forventetDetail = "some reason")
        verify(exactly = 0) { chain.doFilter(request, response) }
    }

    should("report 'forbidden' and break filter chain when 'tilgangssjekk feilet'") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/api/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)
        val writer = mockk<PrintWriter>(relaxed = true)
        every { response.writer } returns writer

        ImpersonalAccessFilter(
            pidGetter = arrangePidError(),
            fagtilgangService = arrangeFagtilgang(innvilget = true),
            populasjonstilgangService = arrangePopulasjonstilgang(result = null),
            auditor = mockk(),
            objectMapper = objectMapper,
        ).doFilter(request, response, chain)

        verify(exactly = 1) { response.status = 403 }
        verifyTilgangsnektBody(writer, forventetDetail = "Tilgang nektet pga. feil - se logg for detaljer")
        verify(exactly = 0) { chain.doFilter(request, response) }
    }

    should("log audit info and continue filter chain when 'fagtilgang og populasjonstilgang innvilget'") {
        val chain = mockk<FilterChain>(relaxed = true)
        val auditor = mockk<Auditor>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            fagtilgangService = arrangeFagtilgang(innvilget = true),
            populasjonstilgangService = arrangePopulasjonstilgang(result = null),
            auditor = auditor,
            objectMapper = objectMapper,
        ).doFilter(request, response, chain)

        verify(exactly = 1) { auditor.audit(onBehalfOfPid = pid, requestUri = "/foo") }
        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    should("skip access check and continue filter chain on ERROR dispatch") {
        // Sikrer at tilgangssjekken ikke kjøres på nytt ved ERROR-dispatch til /error,
        // som ellers ville forkastet responsbodyen med årsak til tilgangsnekt.
        val chain = mockk<FilterChain>(relaxed = true)
        val response = mockk<HttpServletResponse>(relaxed = true)
        val fagtilgangService = mockk<FagtilgangService>()
        val request = arrangeRequest(
            pid = pid.value,
            uri = "/error",
            dispatcherType = DispatcherType.ERROR
        )

        ImpersonalAccessFilter(
            pidGetter = mockk(),
            fagtilgangService = fagtilgangService,
            populasjonstilgangService = mockk(),
            auditor = mockk(),
            objectMapper = objectMapper,
        ).doFilter(request, response, chain)

        verify(exactly = 0) { fagtilgangService.tilgangInnvilget() }
        verify(exactly = 0) { response.sendError(any(), any()) }
        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    should("interrupt filter chain when 'populasjonstilgangssjekk feiler'") {
        val chain = mockk<FilterChain>(relaxed = true)
        val auditor = mockk<Auditor>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            fagtilgangService = arrangeFagtilgang(innvilget = true),
            populasjonstilgangService = arrangePopulasjonstilgang(feil),
            auditor = auditor,
            objectMapper = objectMapper,
        ).doFilter(request, response, chain)

        verify(exactly = 0) { auditor.audit(onBehalfOfPid = pid, requestUri = "/foo") }
        verify(exactly = 0) { chain.doFilter(request, response) }
    }
})

private val avvist =
    TilgangResult(
        innvilget = false,
        avvisningAarsak = AvvisningAarsak.GEOGRAFISK,
        begrunnelse = "some reason"
    )

private val feil =
    TilgangResult(
        innvilget = false,
        avvisningAarsak = AvvisningAarsak.POPULASJONSTILGANGSSJEKK_FEIL,
        begrunnelse = "feil"
    )

private fun verifyTilgangsnektBody(writer: PrintWriter, forventetDetail: String) {
    val body = slot<String>()
    verify(exactly = 1) { writer.write(capture(body)) }

    val tilgangsnektResponse = objectMapper.readTree(body.captured)
    tilgangsnektResponse["type"].asString() shouldBe TilgangsnektResponse.TILGANGSNEKT_TYPE
    tilgangsnektResponse["detail"].asString() shouldBe forventetDetail
}

private fun arrangePid(): PidExtractor =
    mockk { every { pid() } returns pid }

private fun arrangePidError(): PidExtractor =
    mockk { every { pid() } throws RuntimeException("feil") }

private fun arrangeRequest(
    pid: String?,
    uri: String,
    dispatcherType: DispatcherType = DispatcherType.REQUEST
): HttpServletRequest =
    mockk {
        every { getHeader("fnr") } returns pid
        every { requestURI } returns uri
        every { this@mockk.dispatcherType } returns dispatcherType
    }

private fun arrangeFagtilgang(innvilget: Boolean): FagtilgangService =
    mockk { every { tilgangInnvilget() } returns innvilget }

private fun arrangePopulasjonstilgang(result: TilgangResult?): CacheAwarePopulasjonstilgangService =
    mockk { every { eventuellTilgangsnektAarsak(pid) } returns result }