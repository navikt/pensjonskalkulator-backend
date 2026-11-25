package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal

import jakarta.servlet.DispatcherType
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.security.SecurityConfiguration.Companion.FEATURE_URI
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.AvvisningAarsak
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.TilgangResult
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.acl.TilgangsnektDto
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.fag.FagtilgangService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.CacheAwarePopulasjonstilgangService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.util.StringUtils.hasLength
import org.springframework.web.filter.GenericFilterBean
import tools.jackson.databind.ObjectMapper

class ImpersonalAccessFilter(
    private val pidGetter: PidGetter,
    private val fagtilgangService: FagtilgangService,
    private val populasjonstilgangService: CacheAwarePopulasjonstilgangService,
    private val auditor: Auditor,
    private val objectMapper: ObjectMapper
) : GenericFilterBean() {

    private val log = KotlinLogging.logger {}

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request.dispatcherType != DispatcherType.REQUEST) {
            chain.doFilter(request, response)
            return
        }

        // Request for state of feature toggle requires no authentication or access check:
        if ((request as HttpServletRequest).requestURI.startsWith(FEATURE_URI)) {
            chain.doFilter(request, response)
            return
        }

        try {
            if (hasPid(request)) {
                eventuellTilgangsnektAarsak()?.let {
                    forbidden(response, aarsak = it)
                    return
                }

                auditor.audit(onBehalfOfPid = pidGetter.pid(), requestUri = request.requestURI)
            }
        } catch (e: Exception) {
            // Enhver feil skal gi 'tilgang avvist'
            forbidden(response, e)
            return
        }

        chain.doFilter(request, response)
    }

    private fun eventuellTilgangsnektAarsak(): TilgangResult? =
        if (fagtilgangService.tilgangInnvilget())
            populasjonstilgangService.eventuellTilgangsnektAarsak(pid = pidGetter.pid())
        else
            manglendeFaggruppemedlemskap()

    private fun forbidden(response: ServletResponse, aarsak: TilgangResult) {
        log.warn { "Tilgang nektet pga. ${aarsak.begrunnelse}" }
        respondForbidden(response, aarsak = TilgangsnektDto.transferable(aarsak))
    }

    private fun forbidden(response: ServletResponse, e: Exception) {
        "Tilgang nektet pga. feil i tilgangssjekk".let {
            log.error(e) { it }
            respondForbidden(response, aarsak = TilgangsnektDto.transferable("$it - se logg for detaljer"))
        }
    }

    private fun respondForbidden(response: ServletResponse, aarsak: TilgangsnektDto) {
        (response as HttpServletResponse).apply {
            status = HttpStatus.FORBIDDEN.value()
            characterEncoding = Charsets.UTF_8.name()
            contentType = MediaType.APPLICATION_JSON_VALUE
            writer.write(objectMapper.writeValueAsString(aarsak))
        }
    }

    private companion object {

        private fun hasPid(request: HttpServletRequest): Boolean =
            hasLength(request.getHeader(CustomHttpHeaders.PID))

        private fun manglendeFaggruppemedlemskap() =
            TilgangResult(
                innvilget = false,
                avvisningAarsak = AvvisningAarsak.MANGLENDE_FAGGRUPPE_MEDLEMSKAP,
                begrunnelse = "manglende faggruppemedlemskap",
            )
    }
}