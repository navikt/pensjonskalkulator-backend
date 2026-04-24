package no.nav.pensjon.kalkulator.simulering.api.v1.intern

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpService
import no.nav.pensjon.kalkulator.afp.api.dto.InternServiceberegnetAfpResult
import no.nav.pensjon.kalkulator.afp.api.dto.InternServiceberegnetAfpSpec
import no.nav.pensjon.kalkulator.afp.api.map.ServiceberegnetAfpApiMapper
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.result.*
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.result.SimuleringResultMapper.toDto
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.spec.SimuleringSpecMapper.fromDto
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.spec.SimuleringV1Spec
import no.nav.pensjon.kalkulator.tech.json.writeValueAsRedactedString
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tools.jackson.databind.json.JsonMapper

@RestController
@RequestMapping("api/intern")
class InternSimuleringController(
    private val service: SimuleringService,
    private val serviceberegnetAfpService: ServiceberegnetAfpService,
    private val traceAid: TraceAid,
    private val jsonMapper: JsonMapper
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v1/pensjon/simulering")
    @Operation(
        summary = "Simuler pensjon",
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
    fun simulerPensjon(@RequestBody spec: SimuleringV1Spec): ResponseEntity<SimuleringV1Result> {
        traceAid.begin()
        log.debug { "Simulering request ${jsonMapper.writeValueAsRedactedString(spec)}" }

        return try {
            val result = service.simulerPensjon(providedSpec = fromDto(spec))

            val resultDto: SimuleringV1Result = toDto(
                source = result,
                naavaerendeAlderAar = result.alderAar ?: 0,
                mode = MappingMode.INTERNAL
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

    @PostMapping("v1/pensjon/serviceberegnet-afp")
    @Operation(
        summary = "Serviceberegning AFP (FPP-simulering)",
        description = "Beregner AFP via pensjonssimulators simuler-for-fpp-endepunkt."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Beregning utført"),
            ApiResponse(responseCode = "400", description = "Uakseptable inndata"),
            ApiResponse(responseCode = "500", description = "Feil i systemet")
        ]
    )
    fun serviceberegnetAfp(@RequestBody spec: InternServiceberegnetAfpSpec): ResponseEntity<InternServiceberegnetAfpResult> {
        traceAid.begin()
        log.debug { "Serviceberegnet AFP request ${jsonMapper.writeValueAsRedactedString(spec)}" }

        return try {
            val result = serviceberegnetAfpService.simulerServiceberegnetAfp(spec)
            val resultDto = ServiceberegnetAfpApiMapper.toDto(result)
                .also { log.debug { "Serviceberegnet AFP respons ${jsonMapper.writeValueAsRedactedString(it)}" } }

            ResponseEntity.ok(resultDto)
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } catch (e: Exception) {
            log.error(e) { "Intern feil for serviceberegnet AFP spec ${jsonMapper.writeValueAsRedactedString(spec)}" }
            throw e
        } finally {
            traceAid.end()
        }
    }

    @ExceptionHandler(value = [Exception::class])
    private fun internalError(e: Exception): ResponseEntity<SimuleringV1Result> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem(e))

    override fun errorMessage() = ERROR_MESSAGE

    companion object {
        private const val ERROR_MESSAGE = "feil ved simulering"
        private const val SIMULERINGSTYPE_METRIC_NAME = "simulering_type"

        private fun problem(e: Exception) =
            SimuleringV1Result(
                alderspensjonListe = emptyList(),
                maanedligAlderspensjonVedUttaksendring = null,
                tidsbegrensetOffentligAfp = null,
                privatAfpListe = null,
                livsvarigOffentligAfpListe = null,
                vilkaarsproevingsresultat = SimuleringV1Vilkaarsproevingsresultat(
                    erInnvilget = false,
                    alternativ = null
                ),
                trygdetid = null,
                pensjonsgivendeInntektListe = null,
                problem = SimuleringV1Problem(
                    kode = SimuleringV1ProblemType.SERVERFEIL,
                    beskrivelse = e.message ?: e.javaClass.simpleName
                )
            )
    }
}
