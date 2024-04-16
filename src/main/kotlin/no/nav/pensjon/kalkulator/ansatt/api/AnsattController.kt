package no.nav.pensjon.kalkulator.ansatt.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.ansatt.AnsattService
import no.nav.pensjon.kalkulator.ansatt.api.dto.AnsattV1
import no.nav.pensjon.kalkulator.ansatt.api.map.AnsattMapperV1.dtoV1
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class AnsattController(
    private val service: AnsattService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @GetMapping("v1/ansatt-id")
    @Operation(
        summary = "Hent ansatt-ID",
        description = "Henter informasjon som identifiserer den innloggede ansatte."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av ansatt-ID utført."
            ),
            ApiResponse(
                responseCode = "503",
                description = "Henting av ansatt-ID kunne ikke utføres av tekniske årsaker.",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun ansattId(): AnsattV1 {
        traceAid.begin()
        log.debug { "Request for ansatt-ID" }

        return try {
            dtoV1(timed(function = service::getAnsattId, functionName = "ansattId"))
                .also { log.debug { "Ansatt-ID respons: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av ansattinformasjon"
    }
}
