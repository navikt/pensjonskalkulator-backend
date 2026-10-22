package no.nav.pensjon.kalkulator.simulering.api.intern.v2

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result.SimuleringV2Result
import no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec.SimuleringV2Spec
import no.nav.pensjon.kalkulator.tech.json.writeValueAsRedactedString
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tools.jackson.databind.json.JsonMapper

@RestController
@RequestMapping("api/intern")
class InternSimuleringControllerV2(
    private val service: SimuleringService,
    private val traceAid: TraceAid,
    private val jsonMapper: JsonMapper
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v2/pensjon/simulering")
    @Operation(
        summary = "Simuler pensjon (versjon 2)",
        description = "Lager en prognose for framtidig alderspensjon med støtte for AFP."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Simulering utført"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Simulering kunne ikke utføres pga. uakseptable inndata."
            ),
            ApiResponse(
                responseCode = "401",
                description = "Simulering kunne ikke utføres pga. manglende/feilaktig autentisering."
            ),
            ApiResponse(
                responseCode = "403",
                description = "Simulering kunne ikke utføres pga. manglende tilgang til tjenesten."
            ),
            ApiResponse(
                responseCode = "404",
                description = "Simulering kunne ikke utføres fordi angitt person ikke finnes i systemet."
            ),
            ApiResponse(
                responseCode = "500",
                description = "Simulering kunne ikke utføres pga. feil i systemet."
            )
        ]
    )
    fun simulerPensjon(@RequestBody spec: SimuleringV2Spec): ResponseEntity<SimuleringV2Result> {
        traceAid.begin()
        log.debug { "Simulering request ${jsonMapper.writeValueAsRedactedString(spec)}" }

        return try {
            val result = service.simulerPensjon(
                providedSpec = spec.toInternalValue(tillatSenereFoersteuttakForUfoere = true)
            )

            val resultDto: SimuleringV2Result = SimuleringV2Result.transferable(
                source = result,
                naavaerendeAlderAar = result.alderAar ?: 0
            ).also {
                log.debug { "Simulering respons ${jsonMapper.writeValueAsRedactedString(it)}" }
            }

            Metrics.countType(eventName = SIMULERINGSTYPE_METRIC_NAME, type = spec.simuleringstype.name)
            ResponseEntity.status(resultDto.problem?.kode?.httpStatus ?: HttpStatus.OK).body(resultDto)
        } catch (e: Exception) {
            log.error(e) { "Intern feil for spec ${jsonMapper.writeValueAsRedactedString(spec)}" }
            throw e
        } finally {
            traceAid.end()
        }
    }

    @ExceptionHandler(value = [Exception::class])
    private fun internalError(e: Exception): ResponseEntity<SimuleringV2Result> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(SimuleringV2Result.serverfeil(e))

    override fun errorMessage() = ERROR_MESSAGE

    companion object {
        private const val ERROR_MESSAGE = "feil ved simulering v2"
        private const val SIMULERINGSTYPE_METRIC_NAME = "simulering_type"
    }
}