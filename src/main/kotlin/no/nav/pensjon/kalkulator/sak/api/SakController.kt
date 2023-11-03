package no.nav.pensjon.kalkulator.sak.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.sak.SakService
import no.nav.pensjon.kalkulator.sak.api.dto.SakDto
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.sak.api.map.SakMapper.toDto
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class SakController(
    private val service: SakService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    @GetMapping("sak-status")
    @Operation(
        summary = "Har uføretrygd/gjenlevendeytelse",
        description = "Hvorvidt den innloggede brukeren har løpende uføretrygd eller gjenlevendeytelse"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Sjekking av saker utført"
            ),
            ApiResponse(
                responseCode = "503", description = "Sjekking av saker kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun harRelevantSak(): SakDto {
        traceAid.begin()
        log.debug { "Request for sak-status" }

        return try {
            toDto(timed(service::harRelevantSak, "harRelevantSak"))
                .also { log.debug { "Sak-status respons: $it" } }
        } catch (e: EgressException) {
            handleError(e)!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "Feil ved sjekking av saker"
    }
}
