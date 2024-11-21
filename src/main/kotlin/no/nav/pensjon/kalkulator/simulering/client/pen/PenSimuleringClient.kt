package no.nav.pensjon.kalkulator.simulering.client.pen

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringResult
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringError
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimuleringError
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimuleringResult
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenSimuleringResultDto
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringEgressSpecDto
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenAnonymSimuleringResultMapper
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenAnonymSimuleringSpecMapper
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenSimuleringResultMapper
import no.nav.pensjon.kalkulator.simulering.client.pen.map.PenSimuleringSpecMapper
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PenSimuleringClient(
    @Value("\${pen.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClientBuilder, traceAid, retryAttempts), SimuleringClient, Pingable {

    override fun simulerAlderspensjon(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ) =
        doPost(
            PATH,
            PenSimuleringSpecMapper.toDto(impersonalSpec, personalSpec),
            SimuleringEgressSpecDto::class.java,
            PenSimuleringResultDto::class.java
        )?.let(PenSimuleringResultMapper::fromDto)
            ?: emptyResult()



    override fun simulerAnonymAlderspensjon(spec: ImpersonalSimuleringSpec): SimuleringResult {
        return try {
            doPost(
                path = ANONYM_PATH,
                requestBody = PenAnonymSimuleringSpecMapper.toDto(spec),
                requestClass = PenAnonymSimuleringSpec::class.java,
                responseClass = PenAnonymSimuleringResult::class.java,
                deprecatedBasePath = true
            )?.let(PenAnonymSimuleringResultMapper::fromDto)
                ?: emptyResult()
        }  catch (e: EgressException) { //Adding formatted errorObject to the EgressException
            e.errorObj = extractMessageFromException(e)
            throw e
        }
    }

    private fun extractMessageFromException(errorResponse: EgressException): SimuleringError? {
        try {
            val jsonMap: PenAnonymSimuleringError = mapper.readValue(errorResponse.message, PenAnonymSimuleringError::class.java)
            val regex = """PEN(\d+\w*Exception): (.+)""".toRegex()
            val matchResult = regex.find(jsonMap.feilmelding)
            return matchResult?.let {
                val errorCode = ("PKU" + it.groups[1]?.value)
                val errorMessage = it.groups[2]?.value?.trim() ?: ""
                SimuleringError(status = errorCode.toString(), message = errorMessage)
            }
        } catch (ex: JsonMappingException ) { //TODO: Endre til å gi en overordnet respons
            throw EgressException("Kunne ikke parse feilmelding fra PEN", ex)
        } catch (ex: JsonProcessingException) {
            throw EgressException("Kunne ikke parse feilmelding fra PEN", ex)
        }

    }



    private companion object {
        private const val PATH = "simulering/alderspensjon"
        private const val ANONYM_PATH = "simulering/fleksibelap"
        private val mapper = jacksonObjectMapper()


        private fun emptyResult() =
            SimuleringResult(
                alderspensjon = emptyList(),
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(innvilget = false),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningGrunnlagListe = emptyList()
            )
    }
}
