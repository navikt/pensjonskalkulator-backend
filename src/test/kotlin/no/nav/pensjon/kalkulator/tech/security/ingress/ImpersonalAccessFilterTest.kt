package no.nav.pensjon.kalkulator.tech.security.ingress

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.ImpersonalAccessFilter
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.SkjermingService
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
    private lateinit var skjermingService: SkjermingService

    @Test
    fun `when fnr in header then doFilter reports unauthorized and breaks filter chain`() {
        `when`(request.getHeader("fnr")).thenReturn(pid.value)

        ImpersonalAccessFilter(pidExtractor, skjermingService).doFilter(request, response, chain)

        verify(response, times(1)).sendError(
            401,
            "Adgang nektet pga. mulig skjerming, adressebeskyttelse eller manglende gruppemedlemskap"
        )
        verify(chain, never()).doFilter(request, response)
    }
}
