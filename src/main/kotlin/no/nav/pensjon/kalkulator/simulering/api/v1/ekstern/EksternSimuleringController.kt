package no.nav.pensjon.kalkulator.simulering.api.v1.ekstern

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.simulering.SimuleringResult
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.result.MappingMode
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.result.SimuleringResultMapper.toDto
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.result.SimuleringV1Problem
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.result.SimuleringV1ProblemType
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.result.SimuleringV1Result
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.result.SimuleringV1Vilkaarsproevingsresultat
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.spec.SimuleringSpecMapper.fromDto
import no.nav.pensjon.kalkulator.simulering.api.v1.acl.spec.SimuleringV1Spec
import no.nav.pensjon.kalkulator.tech.json.writeValueAsRedactedString
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.time.TodayProvider
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tools.jackson.databind.json.JsonMapper

@RestController
@RequestMapping("api/ekstern")
class EksternSimuleringController(
    private val service: SimuleringService,
    private val feature: FeatureToggleService,
    private val personService: PersonService,
    private val time: TodayProvider,
    private val traceAid: TraceAid,
    private val jsonMapper: JsonMapper
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v1/pensjon/simulering")
    @Operation(
        summary = "Simuler pensjon",
        description = "Lag en prognose for framtidig alderspensjon med støtte for AFP." +
                " Dersom simulering med de angitte parametre resulterer i avslag i" +
                " vilkårsprøvingen, vil responsen inneholde alternative parametre som vil gi et innvilget" +
                " simuleringsresultat"
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

        return try {
            val result: SimuleringResult = service.simulerPersonligAlderspensjon(fromDto(spec))

            val resultDto: SimuleringV1Result =
                if (feature.isEnabled("utvidet-simuleringsresultat"))
                    toDto(
                        source = result,
                        naavaerendeAlderAar = alder().aar,
                        mode = MappingMode.EXTENDED_EXTERNAL
                    )
                else
                    toDto(
                        source = result,
                        naavaerendeAlderAar = alder().aar,
                        mode = MappingMode.NORMAL_EXTERNAL
                    )

            Metrics.countType(eventName = SIMULERING_TYPE_METRIC_NAME, type = spec.simuleringstype.name)
            ResponseEntity.status(resultDto.problem?.kode?.httpStatus ?: HttpStatus.OK).body(resultDto)
        } catch (e: Exception) {
            log.error(e) { "Intern feil for spec ${jsonMapper.writeValueAsRedactedString(spec)}" }
            throw e
        } finally {
            traceAid.end()
        }
    }

    private fun alder() =
        Alder.from(
            foedselDato = personService.getPerson().foedselsdato,
            dato = time.date()
        )

    @ExceptionHandler(value = [Exception::class])
    private fun internalError(e: Exception): ResponseEntity<SimuleringV1Result> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem(e))

    override fun errorMessage() = ERROR_MESSAGE

    companion object {
        private const val ERROR_MESSAGE = "feil ved simulering"
        private const val SIMULERING_TYPE_METRIC_NAME = "simulering_type"

        private fun problem(e: Exception) =
            SimuleringV1Result(
                alderspensjonListe = emptyList(),
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
