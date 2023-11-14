package no.nav.pensjon.kalkulator.opptjening.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.opptjening.api.dto.InntektDto
import no.nav.pensjon.kalkulator.opptjening.api.map.InntektMapper.toDto
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class InntektController(
    private val service: InntektService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @GetMapping("inntekt")
    @Operation(
        summary = "Siste pensjonsgivende inntekt",
        description = "Henter den innloggede brukerens sist skattelignede pensjonsgivende inntekt"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av inntekt utført."
            ),
            ApiResponse(
                responseCode = "503", description = "Henting av inntekt kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun sistePensjonsgivendeInntekt(): InntektDto {
        traceAid.begin()
        log.debug { "Request for inntekt" }

        return try {
            toDto(timed(service::sistePensjonsgivendeInntekt, "sistePensjonsgivendeInntekt"))
                .also { log.debug { "Inntekt respons: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V0")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av inntekt"
    }
}
