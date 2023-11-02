package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.SkjermingService
import no.nav.pensjon.kalkulator.tech.security.SecurityConfiguration.Companion.isImpersonal
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

@Component
class ImpersonalAccessFilter(
    private val pidGetter: PidExtractor,
    private val skjermingService: SkjermingService
) : GenericFilterBean() {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (isImpersonal(request as HttpServletRequest)) {
            //TODO check gruppemedlemskap and adressebeskyttelse
            if (!skjermingService.innloggetBrukerHarTilgangTil(pidGetter.pid())) {
                unauthorized(response as HttpServletResponse)
                return
            }

            unauthorized(response as HttpServletResponse)
            return
        }

        chain.doFilter(request, response)
    }

    private companion object {

        private fun unauthorized(response: HttpServletResponse) {
            response.sendError(
                HttpStatus.UNAUTHORIZED.value(),
                "Adgang nektet pga. mulig skjerming, adressebeskyttelse eller manglende gruppemedlemskap"
            )
        }
    }
}
