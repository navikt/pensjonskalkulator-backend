package no.nav.pensjon.kalkulator.tech.security.ingress

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Fortrolighet
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.SecurityConfiguration.Companion.FEATURE_URI
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.enriched
import no.nav.pensjon.kalkulator.tech.security.ingress.Responder.respondForbidden
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

/**
 * Sjekker om bruker har logget inn med tilstrekkelig sikkerhetsnivå.
 */
class SecurityLevelFilter(
    private val adresseService: FortroligAdresseService,
    private val pidGetter: PidGetter
) : GenericFilterBean() {

    private val log = KotlinLogging.logger {}

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        // Request for state of feature toggle requires no authentication or access check:
        if ((request as HttpServletRequest).requestURI.startsWith(FEATURE_URI)) {
            chain.doFilter(request, response)
            return
        }

        try {
            authentication()?.let {
                if (it.veilederInnlogget().not())
                    it.getClaim(ASSURANCE_LEVEL_CLAIM_KEY)?.let(::validate)
            }
        } catch (e: AccessDeniedException) {
            log.warn { "Access denied - ${e.message}" }
            handleAccessDenied(response, reason = e.message ?: AccessDeniedReason.NONE.name)
            return
        }

        chain.doFilter(request, response)
    }

    private fun validate(assuranceLevel: String) {
        if (!highAssuranceLevelList.contains(assuranceLevel) && harStrengtFortroligAdresse(pidGetter.pid()))
            throw AccessDeniedException(AccessDeniedReason.INSUFFICIENT_LEVEL_OF_ASSURANCE.name)
    }

    private fun harStrengtFortroligAdresse(pid: Pid): Boolean =
        adresseService.adressebeskyttelseGradering(pid).fortrolighet == Fortrolighet.STRENG

    private fun handleAccessDenied(response: ServletResponse, reason: String) {
        log.warn { "Access denied - $reason" }
        respondForbidden(response as HttpServletResponse, reason)
    }

    private companion object {
        private const val ASSURANCE_LEVEL_CLAIM_KEY = "acr" // Authentication Context class Reference

        private val highAssuranceLevelList = listOf<String>("idporten-loa-high", "Level4")

        private fun authentication(): EnrichedAuthentication? =
            SecurityContextHolder.getContext().authentication?.enriched()
    }
}
