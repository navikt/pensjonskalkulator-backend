package no.nav.pensjon.kalkulator.tech.security.ingress

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.kalkulator.tech.security.SecurityConfiguration.Companion.isImpersonal
import org.springframework.http.HttpStatus
import org.springframework.web.filter.GenericFilterBean

class ImpersonalAccessFilter : GenericFilterBean() {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (isImpersonal(request as HttpServletRequest)) {
            //TODO check gruppemedlemskap, skjerming and adressebeskyttelse
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
