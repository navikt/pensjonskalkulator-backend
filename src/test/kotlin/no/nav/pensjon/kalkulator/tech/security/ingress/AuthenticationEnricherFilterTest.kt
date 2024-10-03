package no.nav.pensjon.kalkulator.tech.security.ingress

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.kalkulator.tech.security.egress.SecurityContextEnricher
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.context.junit.jupiter.SpringExtension

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

    @Test
    fun `if access denied then doFilter sets status 403 (Forbidden) and breaks filter chain`() {
        `when`(enricher.enrichAuthentication(request, response)).thenThrow(AccessDeniedException("oops!"))

        AuthenticationEnricherFilter(enricher).doFilter(request, response, chain)

        verify(chain, never()).doFilter(request, response)
        verify(response, times(1)).sendError(403, "oops!")
    }
}
