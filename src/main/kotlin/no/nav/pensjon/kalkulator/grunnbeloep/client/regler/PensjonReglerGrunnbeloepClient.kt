package no.nav.pensjon.kalkulator.grunnbeloep.client.regler

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.pensjon.kalkulator.grunnbeloep.Grunnbeloep
import no.nav.pensjon.kalkulator.grunnbeloep.client.GrunnbeloepClient
import no.nav.pensjon.kalkulator.grunnbeloep.client.GrunnbeloepSpec
import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.dto.GrunnbeloepRequestDto
import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.dto.GrunnbeloepResponseDto
import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.map.GrunnbeloepMapper
import no.nav.pensjon.kalkulator.regler.PensjonReglerClient
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
class PensjonReglerGrunnbeloepClient(
    @Value("\${pensjon-regler.url}") baseUrl: String,
    webClient: WebClient,
    @Qualifier("regler") objectMapper: ObjectMapper,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : PensjonReglerClient(baseUrl, webClient, objectMapper, traceAid, retryAttempts), GrunnbeloepClient {

    override fun getGrunnbeloep(spec: GrunnbeloepSpec): Grunnbeloep {
        val requestSpec = GrunnbeloepMapper.toDto(spec)

        val response =
            doPost(GRUNNBELOEP_PATH, requestSpec, GrunnbeloepRequestDto::class.java, GrunnbeloepResponseDto::class.java)

        return GrunnbeloepMapper.fromDto(response)
    }

    companion object {
        private const val GRUNNBELOEP_PATH = "api/hentGrunnbelopListe"
    }
}
