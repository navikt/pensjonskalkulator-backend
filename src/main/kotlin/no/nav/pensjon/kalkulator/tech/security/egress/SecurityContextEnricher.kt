package no.nav.pensjon.kalkulator.tech.security.egress

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.crypto.CryptoService
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonService
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentertRolle
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.security.ingress.AccessDeniedReason
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityContextPidExtractor
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils.hasLength

@Component
class SecurityContextEnricher(
    val tokenSuppliers: EgressTokenSuppliersByService,
    private val securityContextPidExtractor: SecurityContextPidExtractor,
    private val pidDecrypter: CryptoService,
    private val representasjonService: RepresentasjonService
) {
    fun enrichAuthentication(request: HttpServletRequest, response: HttpServletResponse) {
        with(SecurityContextHolder.getContext()) {
            if (authentication == null) {
                authentication = anonymousAuthentication()
            } else {
                authentication = enrich(authentication!!, request)
                authentication = applyPotentialFullmakt(authentication!!, request)
            }
        }
    }

    private fun enrich(auth: Authentication, request: HttpServletRequest) =
        EnrichedAuthentication(
            initialAuth = auth,
            egressTokenSuppliersByService = tokenSuppliers,
            target = headerPid(request)?.let(::personUnderVeiledning) ?: selv()
        )

    private fun applyPotentialFullmakt(
        auth: Authentication,
        request: HttpServletRequest
    ): Authentication =
        onBehalfOfPid(request.cookies)?.let {
            if (validRepresentasjonForhold(it))
                enrichWithFullmakt(auth, it).also { Metrics.countEvent(eventName = "obo", result = "ok") }
            else
                invalidRepresentasjonForhold()
        } ?: auth

    /**
     * NB: Dette støtter ikke brukstilfellet der veileder er logget inn på vegne av en fullmektig.
     * Dette fordi pensjon-representasjon henter ut PID fra TokenX-tokenet (som ikke finnes når veileder er logget inn).
     */
    private fun validRepresentasjonForhold(pid: Pid) =
        representasjonService.hasValidRepresentasjonsforhold(pid).isValid

    private fun enrichWithFullmakt(auth: Authentication, fullmaktGiverPid: Pid) =
        EnrichedAuthentication(
            initialAuth = auth,
            egressTokenSuppliersByService = tokenSuppliers,
            target = RepresentasjonTarget(pid = fullmaktGiverPid, rolle = RepresentertRolle.FULLMAKT_GIVER)
        )

    private fun selv() =
        RepresentasjonTarget(
            pid = securityContextPidExtractor.pid(),
            rolle = RepresentertRolle.SELV
        )

    private fun anonymousAuthentication() =
        EnrichedAuthentication(
            initialAuth = null,
            egressTokenSuppliersByService = tokenSuppliers,
            target = RepresentasjonTarget(rolle = RepresentertRolle.NONE)
        )

    private fun headerPid(request: HttpServletRequest): Pid? =
        request.getHeader(CustomHttpHeaders.PID)?.let {
            when {
                hasLength(it).not() -> null
                else -> if (it.contains(ENCRYPTION_MARK)) pidDecrypter.decrypt(it) else it
            }
        }?.let(::Pid)

    private fun onBehalfOfPid(cookies: Array<Cookie>?): Pid? =
        cookies.orEmpty()
            .filter { ON_BEHALF_OF_COOKIE_NAME.equals(it.name, ignoreCase = true) }
            .map { Pid(it.value) }
            .firstOrNull()

    private companion object {
        private const val ENCRYPTION_MARK = "."
        private const val ON_BEHALF_OF_COOKIE_NAME = "nav-obo"

        private fun personUnderVeiledning(pid: Pid) =
            RepresentasjonTarget(pid, rolle = RepresentertRolle.UNDER_VEILEDNING)

        private fun invalidRepresentasjonForhold(): Nothing {
            Metrics.countEvent(eventName = "obo", result = "avvist")
            throw AccessDeniedException(AccessDeniedReason.INVALID_REPRESENTASJON.name)
        }
    }
}
