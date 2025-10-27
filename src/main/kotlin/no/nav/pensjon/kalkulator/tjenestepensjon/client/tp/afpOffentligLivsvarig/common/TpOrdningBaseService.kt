package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.afpOffentligLivsvarig.common

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.egress.EgressAccess
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.afpOffentligLivsvarig.config.TpOrdningConfig
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.afpOffentligLivsvarig.dto.TpAfpOffentligLivsvarigDto
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.afpOffentligLivsvarig.map.TpAfpOffentligLivsvarigMapper
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.LocalDate

/**
 * Base class for Tjenestepensjon ordning services.
 */
class TpOrdningBaseService(
    protected val config: TpOrdningConfig,
    protected val webClient: WebClient,
    protected val traceAid: TraceAid
) {

    protected val log = KotlinLogging.logger {}

    fun providerName(): String = config.name

    fun hent(pid: Pid, tpNr: String, uttaksdato: LocalDate): AfpOffentligLivsvarigResult {
        val url = buildUrl(pid, tpNr, uttaksdato)
        log.debug { "${providerName()}: GET from URL: '$url'" }

        return try {
            val response = webClient
                .get()
                .uri(url)
                .headers { setHeaders(it, pid) }
                .retrieve()
                .bodyToMono(TpAfpOffentligLivsvarigDto::class.java)
                .block()

            parseResponse(response)
        } catch (e: WebClientRequestException) {
            log.warn(e) { "${providerName()}: Request failed for URL: $url" }
            AfpOffentligLivsvarigResult(null, null)
        } catch (e: WebClientResponseException) {
            log.warn(e) { "${providerName()}: Response error ${e.statusCode} for URL: $url" }
            AfpOffentligLivsvarigResult(null, null)
        } catch (e: Exception) {
            log.warn(e) { "${providerName()}: Unexpected error for URL: $url" }
            AfpOffentligLivsvarigResult(null, null)
        }
    }

    /**
     * Bygger URL ved å erstatte placeholders i config URL med riktige verdier.
     */
    protected fun buildUrl(pid: Pid, tpNr: String, uttaksdato: LocalDate): String {
        return config.url
            .replace("{tpnr}", tpNr)
            .replace("{fnr}", pid.value)
            .replace("{uttaksdato}", uttaksdato.toString())
    }

    protected fun setHeaders(headers: HttpHeaders, pid: Pid) {
        with(EgressAccess.token(EgressService.TJENESTEPENSJON).value) {
            headers.setBearerAuth(this)
            log.debug { "${providerName()}: Token: $this" }
        }
        headers[CustomHttpHeaders.CALL_ID] = traceAid.callId()
        headers[CustomHttpHeaders.PID] = pid.value
    }

    protected fun parseResponse(response: TpAfpOffentligLivsvarigDto?): AfpOffentligLivsvarigResult {
        return TpAfpOffentligLivsvarigMapper.fromDto(response)
    }
}
