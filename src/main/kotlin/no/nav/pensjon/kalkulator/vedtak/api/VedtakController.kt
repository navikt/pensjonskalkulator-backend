package no.nav.pensjon.kalkulator.vedtak.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakService
import no.nav.pensjon.kalkulator.vedtak.VedtakMedUtbetalingService
import no.nav.pensjon.kalkulator.vedtak.api.dto.LoependeVedtakV1
import no.nav.pensjon.kalkulator.vedtak.api.dto.LoependeVedtakV2
import no.nav.pensjon.kalkulator.vedtak.api.map.LoependeVedtakMapperV1
import no.nav.pensjon.kalkulator.vedtak.api.map.LoependeVedtakMapperV2
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class VedtakController(
    val traceAid: TraceAid,
    val loependeVedtakService: LoependeVedtakService,
    val service: VedtakMedUtbetalingService
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @GetMapping("/v1/vedtak/loepende-vedtak")
    @Operation(
        summary = "Har løpende vedtak",
        description = "Hvorvidt den innloggede brukeren har løpende uføretrygd med uttaksgrad, alderspensjon med uttaksgrad, AFP i privat eller offentlig sektor"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av løpende vedtak utført"
            ),
            ApiResponse(
                responseCode = "503", description = "Henting av løpende vedtak kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun hentLoependeVedtakV1(): LoependeVedtakV1 {
        traceAid.begin()
        val version = "V1"
        log.debug { "Request for hent løpende vedtak $version" }

        return try {
            LoependeVedtakMapperV1.toDto(timed(loependeVedtakService::hentLoependeVedtak, "hentLoependeVedtakV1"))
                .also { log.debug { "Hent løpende vedtak V1 respons $version" } }
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    @GetMapping("/v2/vedtak/loepende-vedtak")
    @Operation(
        summary = "Har løpende vedtak",
        description = "Hvorvidt den innloggede brukeren har løpende uføretrygd med uttaksgrad, alderspensjon med uttaksgrad, AFP i privat eller offentlig sektor"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av løpende vedtak utført"
            ),
            ApiResponse(
                responseCode = "503", description = "Henting av løpende vedtak kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    suspend fun hentLoependeVedtakV2(): LoependeVedtakV2 {
        traceAid.begin()
        val version = "V2"
        log.debug { "Request for hent løpende vedtak $version" }

        return try {
            LoependeVedtakMapperV2.toDto(timed(service::hentVedtakMedUtbetaling, "hentLoependeVedtakV2"))
                .also { log.debug { "Hent løpende vedtak V2 respons $version" } }
        } catch (e: EgressException) {
            handleError(e, "V2")!!
        } finally {
            traceAid.end()
        }
    }


    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "Feil ved henting av løpende vedtak"
    }
}
