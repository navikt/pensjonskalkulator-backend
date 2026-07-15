package no.nav.pensjon.kalkulator.tech.security.egress

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.PossiblyEncryptedPid
import no.nav.pensjon.kalkulator.tech.crypto.CryptoService
import no.nav.pensjon.kalkulator.tech.crypto.EncryptionDetector.isEncryptedPid
import no.nav.pensjon.kalkulator.tech.env.EnvironmentUtil.isProduction
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon
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
    private val log = KotlinLogging.logger {}

    fun enrichAuthentication(request: HttpServletRequest, response: HttpServletResponse) {
        with(SecurityContextHolder.getContext()) {
            if (authentication == null) {
                authentication = anonymousAuthentication()
            } else {
                authentication = authentication?.let { enrich(it, request) }

                if (authentication?.enriched()?.veilederInnlogget() != true) {
                    authentication = authentication?.let { applyPotentialFullmakt(it, request) }
                }
            }
        }
    }

    /**
     * Enrich authentication initially to be able to call service for decrypting PID.
     */
    private fun enrichTemporarily(auth: Authentication) =
        EnrichedAuthentication(
            initialAuth = auth,
            egressTokenSuppliersByService = tokenSuppliers,
            target = selv()
        )

    /**
     * Final enrichment of authentication.
     */
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
        onBehalfOfPid(request.cookies)?.let { applyPotentialFullmakt(auth, pid = it) } ?: auth

    private fun applyPotentialFullmakt(
        auth: Authentication,
        pid: PossiblyEncryptedPid
    ): EnrichedAuthentication =
        representasjon(fullmaktsgiverPid = pid).let {
            if (it.isValid)
                enrichWithFullmakt(auth, fullmaktsgiverPid = it.fullmaktsgiver!!.pid).also {
                    Metrics.countEvent(eventName = "obo", result = "ok")
                }
            else
                invalidRepresentasjonForhold()
        }

    /**
     * NB: Dette støtter ikke brukstilfellet der veileder er innlogget på vegne av en fullmektig.
     * Årsak: pensjon-representasjon henter ut fullmektigens PID fra TokenX-token (finnes ikke når veileder innlogget).
     */
    private fun representasjon(fullmaktsgiverPid: PossiblyEncryptedPid): Representasjon =
        representasjonService.hasValidRepresentasjonsforhold(fullmaktsgiverPid)

    private fun enrichWithFullmakt(auth: Authentication, fullmaktsgiverPid: Pid) =
        EnrichedAuthentication(
            initialAuth = auth,
            egressTokenSuppliersByService = tokenSuppliers,
            target = RepresentasjonTarget(pid = fullmaktsgiverPid, rolle = RepresentertRolle.FULLMAKT_GIVER)
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

    /**
     * Header PID is used in 'veiledning' context.
     * The PID has been encrypted by pensjon-pid-encryption.
     */
    private fun headerPid(request: HttpServletRequest): Pid? =
        request.getHeader(CustomHttpHeaders.PID)?.let {
            when {
                hasLength(it).not() -> null
                else -> if (isEncryptedPid(it)) {
                    with(SecurityContextHolder.getContext()) {
                        authentication = authentication?.let(::enrichTemporarily)
                        pidDecrypter.decrypt(it)
                    }
                } else it
            }
        }?.let(::Pid)

    /**
     * Cookie PID is used in 'representasjon' context.
     * The PID has been encrypted by pensjon-representasjon (not by pensjon-pid-encryption).
     */
    private fun onBehalfOfPid(cookies: Array<Cookie>?): PossiblyEncryptedPid? =
        cookies.orEmpty()
            .firstOrNull { ON_BEHALF_OF_COOKIE_NAME.equals(it.name, ignoreCase = true) }
            ?.value
            ?.let(::possiblyEncryptedPid)

    private fun possiblyEncryptedPid(value: String) =
        PossiblyEncryptedPid(value).also {
            if (isProduction() && it.isEncrypted.not())
                log.warn { "Unencrypted PID received in OBO cookie" }
        }

    private companion object {
        private const val ON_BEHALF_OF_COOKIE_NAME = "nav-obo"

        private fun personUnderVeiledning(pid: Pid) =
            RepresentasjonTarget(pid, rolle = RepresentertRolle.UNDER_VEILEDNING)

        private fun invalidRepresentasjonForhold(): Nothing {
            Metrics.countEvent(eventName = "obo", result = "avvist")
            throw AccessDeniedException(AccessDeniedReason.INVALID_REPRESENTASJON.name)
        }
    }
}