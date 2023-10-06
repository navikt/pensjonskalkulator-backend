package no.nav.pensjon.kalkulator.ufoere.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.ufoere.UfoerepensjonService
import no.nav.pensjon.kalkulator.ufoere.api.dto.UfoerepensjonDto
import no.nav.pensjon.kalkulator.ufoere.api.dto.UfoerepensjonSpecDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class UfoerepensjonController(
    private val service: UfoerepensjonService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    @PostMapping("ufoerepensjon")
    @Operation(
        summary = "Har løpende uføretrygd",
        description = "Hvorvidt den innloggede brukeren har løpende uføretrygd"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Sjekking av uføretrygd utført"
            ),
            ApiResponse(
                responseCode = "503", description = "Sjekking av uføretrygd kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun harUfoeretrygd(@RequestBody spec: UfoerepensjonSpecDto): UfoerepensjonDto {
        traceAid.initialize()
        log.debug { "Request for uføretrygd-status" }

        return try {
            toDto(timed(service::harLoependeUfoerepensjon, spec.fom, "harLoependeUfoerepensjon"))
                .also { log.debug { "Uføretrygd-status respons: $it" } }
        } catch (e: EgressException) {
            handleError(e)!!
        } finally {
            traceAid.finalize()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved bestemmelse av uføretrygd-status"

        private fun toDto(harUfoerepensjon: Boolean) = UfoerepensjonDto(harUfoerepensjon)
    }
}
