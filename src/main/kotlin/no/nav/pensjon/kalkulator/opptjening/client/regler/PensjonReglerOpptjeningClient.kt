package no.nav.pensjon.kalkulator.opptjening.client.regler

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningClient
import no.nav.pensjon.kalkulator.opptjening.Opptjeningshistorikk
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningshistorikkSpec
import no.nav.pensjon.kalkulator.opptjening.client.regler.dto.OpptjeningRequestDto
import no.nav.pensjon.kalkulator.opptjening.client.regler.dto.OpptjeningResponseDto
import no.nav.pensjon.kalkulator.opptjening.client.regler.map.OpptjeningMapper
import no.nav.pensjon.kalkulator.regler.PensjonReglerClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
class PensjonReglerOpptjeningClient(
    @Value("\${pensjon-regler.url}") baseUrl: String,
    webClient: WebClient,
    @Qualifier("regler") objectMapper: ObjectMapper
) : PensjonReglerClient(baseUrl, webClient, objectMapper), OpptjeningClient {

    override fun getOpptjeningshistorikk(spec: OpptjeningshistorikkSpec): Opptjeningshistorikk {
        val requestSpec = OpptjeningMapper.toDto(spec)

        val response =
            doPost(OPPTJENING_PATH, requestSpec, OpptjeningRequestDto::class.java, OpptjeningResponseDto::class.java)

        return OpptjeningMapper.fromDto(response)
    }

    companion object {
        private const val OPPTJENING_PATH = "/api/beregnPoengtallBatch"
    }
}
