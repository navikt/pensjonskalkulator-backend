package no.nav.pensjon.kalkulator.opptjening.api.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.opptjening.OpptjeningService
import no.nav.pensjon.kalkulator.opptjening.api.v1.acl.OpptjeningResultMapper.toDto
import no.nav.pensjon.kalkulator.opptjening.api.v1.acl.OpptjeningV1
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/intern")
class OpptjeningController(
    private val service: OpptjeningService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    @GetMapping("v1/opptjening")
    @Operation(
        summary = "Opptjening",
        description = "Henter den innloggede brukerens pensjonsopptjening"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av opptjening utført."
            ),
            ApiResponse(
                responseCode = "503", description = "Henting av opptjening kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun opptjening(): List<OpptjeningV1> {
        traceAid.begin()

        return try {
            service.opptjening().map(::toDto)
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av opptjening"
    }
}