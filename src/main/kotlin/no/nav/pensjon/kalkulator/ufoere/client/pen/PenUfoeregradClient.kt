package no.nav.pensjon.kalkulator.ufoere.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.ufoere.Ufoeregrad
import no.nav.pensjon.kalkulator.ufoere.client.UfoeregradClient
import no.nav.pensjon.kalkulator.ufoere.client.pen.map.UfoeregradMapper
import no.nav.pensjon.kalkulator.ufoere.client.pen.map.UfoeregradPenDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PenUfoeregradClient(
    @Value("\${pen.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClientBuilder, traceAid, retryAttempts), UfoeregradClient {

    override fun hentUfoeregrad(pid: Pid): Ufoeregrad =
        doGet(
            object : ParameterizedTypeReference<UfoeregradPenDto>() {},
            PATH,
            pid
        )?.let(UfoeregradMapper::fromDto) ?: throw EgressException("Kunne ikke hente uføregrad for brukeren")

    private companion object {
        private const val PATH = "uforetrygd/uforegrad/seneste"
    }
}
