package no.nav.pensjon.kalkulator.vedtak.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.vedtak.Innledningsdata
import no.nav.pensjon.kalkulator.vedtak.client.InnledningClient
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenInnledningsdataDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.map.PenInnledningsdataMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PenInnledningClient(
    @Value($$"${pen.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    traceAid: TraceAid,
    @param:Value($$"${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClientBuilder, traceAid, retryAttempts), InnledningClient {

    override fun hentInnledningsdata(pid: Pid): Innledningsdata {
        return doGet(
            object : ParameterizedTypeReference<PenInnledningsdataDto>() {},
            path = PATH,
            pid,
            pidHeaderName = CustomHttpHeaders.IDENT
        )?.let(PenInnledningsdataMapper::fromDto)
            ?: throw EgressException("Kunne ikke hente innledningsdata for brukeren")
    }

    private companion object {
        private const val PATH = "selvbetjening/pensjonskalkulator/innledning"
    }
}
