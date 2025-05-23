package no.nav.pensjon.kalkulator.tech.security.ingress

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.kalkulator.tech.security.egress.SecurityContextEnricher
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.PrintWriter

@ExtendWith(SpringExtension::class)
class AuthenticationEnricherFilterTest {

    @Mock
    private lateinit var enricher: SecurityContextEnricher

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var chain: FilterChain

    @Mock
    private lateinit var writer: PrintWriter

    @Test
    fun `if access denied then doFilter sets status 403 (Forbidden) and breaks filter chain`() {
        `when`(request.requestURI).thenReturn("/api/foo")
        `when`(enricher.enrichAuthentication(request, response)).thenThrow(AccessDeniedException("oops!"))
        `when`(response.writer).thenReturn(writer)

        AuthenticationEnricherFilter(enricher).doFilter(request, response, chain)

        verify(chain, never()).doFilter(request, response)
        verify(response, times(1)).status = 403
        verify(response, times(1)).contentType = MediaType.APPLICATION_JSON_VALUE
        verify(writer, times(1)).append("""{ "reason": "oops!" }""")
    }

    @Test
    fun `if 'feature' request then access check is skipped and filter chain continues`() {
        `when`(request.requestURI).thenReturn("/api/feature/foo")

        AuthenticationEnricherFilter(enricher).doFilter(request, response, chain)

        verify(enricher, never()).enrichAuthentication(request, response)
        verify(chain, times(1)).doFilter(request, response)
    }
}
