package no.nav.pensjon.kalkulator.tech.security.ingress

import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.ImpersonalAccessFilter
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.ShadowTilgangComparator

class ImpersonalAccessFilterTest : ShouldSpec({

    should("continue filter chain when no fnr in header") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = null, uri = "/api/foo")
        val response = mockk<ServletResponse>()

        ImpersonalAccessFilter(
            pidGetter = mockk(),
            groupMembershipService = mockk(),
            auditor = mockk(),
            shadowTilgangComparator = mockk(relaxed = true)
        ).doFilter(request, response, chain)

        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    should("report 'forbidden' and break filter chain when innlogget bruker mangler gruppemedlemskap") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/api/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            groupMembershipService = arrangeTilgang(false),
            auditor = mockk(),
            shadowTilgangComparator = mockk(relaxed = true)
        ).doFilter(request, response, chain)

        verify(exactly = 1) { response.sendError(403, "Adgang nektet pga. manglende gruppemedlemskap") }
        verify(exactly = 0) { chain.doFilter(request, response) }
    }

    should("report 'not found' and break filter chain when person not found") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/api/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            groupMembershipService = arrangeMissingPerson(),
            auditor = mockk(),
            shadowTilgangComparator = mockk(relaxed = true)
        ).doFilter(request, response, chain)

        verify(exactly = 1) { response.sendError(404, "Person ikke funnet") }
        verify(exactly = 0) { chain.doFilter(request, response) }
    }

    should("log audit info and continue filter chain when innlogget bruker har tilgang") {
        val chain = mockk<FilterChain>(relaxed = true)
        val auditor = mockk<Auditor>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            groupMembershipService = arrangeTilgang(true),
            auditor,
            shadowTilgangComparator = mockk(relaxed = true)
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
            groupMembershipService = mockk(),
            auditor = mockk(),
            shadowTilgangComparator = mockk(relaxed = true)
        ).doFilter(request, response, chain)

        verify(exactly = 0) { pidExtractor.pid() }
        verify(exactly = 1) { chain.doFilter(request, response) }
    }

    should("call shadow tilgang comparator when user has access") {
        val chain = mockk<FilterChain>(relaxed = true)
        val auditor = mockk<Auditor>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)
        val shadowComparator = mockk<ShadowTilgangComparator>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            groupMembershipService = arrangeTilgang(true),
            auditor,
            shadowTilgangComparator = shadowComparator
        ).doFilter(request, response, chain)

        verify(exactly = 1) { shadowComparator.compareAsync(pid, true) }
    }

    should("call shadow tilgang comparator when user is denied access") {
        val chain = mockk<FilterChain>(relaxed = true)
        val request = arrangeRequest(pid = pid.value, uri = "/foo")
        val response = mockk<HttpServletResponse>(relaxed = true)
        val shadowComparator = mockk<ShadowTilgangComparator>(relaxed = true)

        ImpersonalAccessFilter(
            pidGetter = arrangePid(),
            groupMembershipService = arrangeTilgang(false),
            auditor = mockk(),
            shadowTilgangComparator = shadowComparator
        ).doFilter(request, response, chain)

        verify(exactly = 1) { shadowComparator.compareAsync(pid, false) }
    }
})

private fun arrangePid(): PidExtractor =
    mockk<PidExtractor>().apply {
        every { pid() } returns pid
    }

private fun arrangeRequest(pid: String?, uri: String): HttpServletRequest =
    mockk<HttpServletRequest>().apply {
        every { getHeader("fnr") } returns pid
        every { requestURI } returns uri
    }

private fun arrangeTilgang(harTilgang: Boolean): GroupMembershipService =
    mockk<GroupMembershipService>().apply {
        every { innloggetBrukerHarTilgang(pid) } returns harTilgang
    }

private fun arrangeMissingPerson(): GroupMembershipService =
    mockk<GroupMembershipService>().apply {
        every { innloggetBrukerHarTilgang(pid) } throws NotFoundException("person")
    }
