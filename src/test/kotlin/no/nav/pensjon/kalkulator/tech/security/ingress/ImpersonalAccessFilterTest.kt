package no.nav.pensjon.kalkulator.tech.security.ingress

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.ImpersonalAccessFilter
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class ImpersonalAccessFilterTest {

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var chain: FilterChain

    @Mock
    private lateinit var pidExtractor: PidExtractor

    @Mock
    private lateinit var groupMembershipService: GroupMembershipService

    @Test
    fun `when fnr in header then doFilter reports 'forbidden' and breaks filter chain`() {
        `when`(request.getHeader("fnr")).thenReturn(pid.value)
        `when`(pidExtractor.pid()).thenReturn(pid)
        `when`(groupMembershipService.innloggetBrukerHarTilgang(pid)).thenReturn(false)

        ImpersonalAccessFilter(pidExtractor, groupMembershipService).doFilter(request, response, chain)

        verify(response, times(1)).sendError(403, "Adgang nektet pga. manglende gruppemedlemskap")
        verify(chain, never()).doFilter(request, response)
    }

    @Test
    fun `when innlogget bruker mangler gruppemedlemskap then doFilter reports 'forbidden' and breaks filter chain`() {
        `when`(request.getHeader("fnr")).thenReturn(pid.value)
        `when`(pidExtractor.pid()).thenReturn(pid)
        `when`(groupMembershipService.innloggetBrukerHarTilgang(pid)).thenReturn(false)

        ImpersonalAccessFilter(pidExtractor, groupMembershipService).doFilter(request, response, chain)

        verify(response, times(1)).sendError(403, "Adgang nektet pga. manglende gruppemedlemskap")
        verify(chain, never()).doFilter(request, response)
    }

    @Test
    fun `when innlogget bruker har tilgang then filter chain continues`() {
        `when`(request.getHeader("fnr")).thenReturn(pid.value)
        `when`(pidExtractor.pid()).thenReturn(pid)
        `when`(groupMembershipService.innloggetBrukerHarTilgang(pid)).thenReturn(true)

        ImpersonalAccessFilter(pidExtractor, groupMembershipService).doFilter(request, response, chain)

        verify(chain, times(1)).doFilter(request, response)
    }
}
