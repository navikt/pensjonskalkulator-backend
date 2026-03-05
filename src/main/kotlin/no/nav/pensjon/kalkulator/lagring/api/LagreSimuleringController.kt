package no.nav.pensjon.kalkulator.lagring.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.lagring.LagreSimuleringService
import no.nav.pensjon.kalkulator.lagring.api.dto.LagreSimuleringResponseDtoV1
import no.nav.pensjon.kalkulator.lagring.api.dto.LagreSimuleringSpecDtoV1
import no.nav.pensjon.kalkulator.lagring.api.map.LagreSimuleringMapperV1.fromDto
import no.nav.pensjon.kalkulator.lagring.api.map.LagreSimuleringMapperV1.toDto
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/intern")
class LagreSimuleringController(
    private val service: LagreSimuleringService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("/v1/lagre-simulering")
    @Operation(
        summary = "Lagre simuleringsresultat",
        description = "Lagrer et simuleringsresultat via Skribenten-tjenesten."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Lagring utført"
            ),
            ApiResponse(
                responseCode = "503",
                description = "Lagring kunne ikke utføres av tekniske årsaker"
            )
        ]
    )
    fun lagreSimuleringV1(@RequestBody spec: LagreSimuleringSpecDtoV1): LagreSimuleringResponseDtoV1 {
        traceAid.begin()
        log.debug { "Request for lagre-simulering" }

        return try {
            timed({ toDto(service.lagreSimulering(fromDto(spec))) }, "lagreSimulering")
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "Feil ved lagring av simuleringsresultat"
    }
}
