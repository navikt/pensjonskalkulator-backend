package no.nav.pensjon.kalkulator.opptjening.client.regler

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.pensjon.kalkulator.opptjening.Opptjeningshistorikk
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningClient
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningshistorikkSpec
import no.nav.pensjon.kalkulator.opptjening.client.regler.dto.OpptjeningRequestDto
import no.nav.pensjon.kalkulator.opptjening.client.regler.dto.OpptjeningResponseDto
import no.nav.pensjon.kalkulator.opptjening.client.regler.map.OpptjeningMapper
import no.nav.pensjon.kalkulator.regler.PensjonReglerClient
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PensjonReglerOpptjeningClient(
    @Value("\${pensjon-regler.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    @Qualifier("regler") objectMapper: ObjectMapper,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") retryAttempts: String
) : PensjonReglerClient(baseUrl, webClientBuilder, objectMapper, traceAid, retryAttempts), OpptjeningClient {

    override fun getOpptjeningshistorikk(spec: OpptjeningshistorikkSpec): Opptjeningshistorikk {
        val requestSpec = OpptjeningMapper.toDto(spec)

        val response =
            doPost(OPPTJENING_PATH, requestSpec, OpptjeningRequestDto::class.java, OpptjeningResponseDto::class.java)

        return OpptjeningMapper.fromDto(response)
    }

    companion object {
        private const val OPPTJENING_PATH = "api/beregnPoengtallBatch"
    }
}
