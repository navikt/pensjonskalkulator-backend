package no.nav.pensjon.kalkulator.tech.security.ingress

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.kalkulator.mock.MockAuthentication
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentertRolle
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class SecurityLevelFilterTest {

    @Mock
    private lateinit var request: HttpServletRequest

    @Mock
    private lateinit var response: HttpServletResponse

    @Mock
    private lateinit var chain: FilterChain

    @Mock
    private lateinit var adresseService: FortroligAdresseService

    @Mock
    private lateinit var pidGetter: PidGetter

    @Test
    fun `when strengt fortrolig adresse and insufficient security level then doFilter breaks filter chain`() {
        arrangeAdresse(AdressebeskyttelseGradering.STRENGT_FORTROLIG)
        arrangeSecurityContext("idporten-loa-substantial", RepresentertRolle.SELV) // insufficient

        SecurityLevelFilter(adresseService, pidGetter).doFilter(request, response, chain)
        verify(chain, never()).doFilter(request, response)
    }

    @Test
    fun `when strengt fortrolig adresse and high security level then doFilter continues filter chain`() {
        arrangeAdresse(AdressebeskyttelseGradering.STRENGT_FORTROLIG)
        arrangeSecurityContext("idporten-loa-high", RepresentertRolle.SELV)

        SecurityLevelFilter(adresseService, pidGetter).doFilter(request, response, chain)

        verify(chain, times(1)).doFilter(request, response)
    }

    @Test
    fun `when strengt fortrolig adresse and security level 4 then doFilter continues filter chain`() {
        arrangeAdresse(AdressebeskyttelseGradering.STRENGT_FORTROLIG)
        arrangeSecurityContext("Level4", RepresentertRolle.FULLMAKT_GIVER)

        SecurityLevelFilter(adresseService, pidGetter).doFilter(request, response, chain)

        verify(chain, times(1)).doFilter(request, response)
    }

    @Test
    fun `when strengt fortrolig adresse and security level 3 then doFilter breaks filter chain`() {
        arrangeAdresse(AdressebeskyttelseGradering.STRENGT_FORTROLIG)
        arrangeSecurityContext("Level3", RepresentertRolle.FULLMAKT_GIVER)

        SecurityLevelFilter(adresseService, pidGetter).doFilter(request, response, chain)

        verify(chain, never()).doFilter(request, response)
    }

    @Test
    fun `when fortrolig adresse and substantial security level then doFilter continues filter chain`() {
        arrangeAdresse(AdressebeskyttelseGradering.FORTROLIG)
        arrangeSecurityContext("idporten-loa-substantial", RepresentertRolle.SELV)

        SecurityLevelFilter(adresseService, pidGetter).doFilter(request, response, chain)

        verify(chain, times(1)).doFilter(request, response)
    }

    @Test
    fun `when veileder innlogget then doFilter continues filter chain`() {
        arrangeSecurityContext("", RepresentertRolle.UNDER_VEILEDNING)
        SecurityLevelFilter(adresseService, pidGetter).doFilter(request, response, chain)
        verify(chain, times(1)).doFilter(request, response)
    }

    private fun arrangeAdresse(gradering: AdressebeskyttelseGradering) {
        `when`(adresseService.adressebeskyttelseGradering(pid)).thenReturn(gradering)
        `when`(pidGetter.pid()).thenReturn(pid)
    }

    private companion object {
        private fun arrangeSecurityContext(securityLevel: String, rolle: RepresentertRolle) {
            SecurityContextHolder.setContext(
                SecurityContextImpl(
                    EnrichedAuthentication(
                        initialAuth = MockAuthentication("acr", securityLevel),
                        egressTokenSuppliersByService = EgressTokenSuppliersByService(emptyMap()),
                        target = RepresentasjonTarget(null, rolle)
                    )
                )
            )
        }
    }
}
