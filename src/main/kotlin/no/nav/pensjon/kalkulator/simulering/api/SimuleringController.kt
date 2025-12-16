package no.nav.pensjon.kalkulator.simulering.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.simulering.SimuleringException
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import no.nav.pensjon.kalkulator.simulering.api.map.AnonymSimuleringResultMapperV1.errorV1
import no.nav.pensjon.kalkulator.simulering.api.map.AnonymSimuleringResultMapperV1.resultatV1
import no.nav.pensjon.kalkulator.simulering.api.map.AnonymSimuleringSpecMapperV1
import no.nav.pensjon.kalkulator.simulering.api.map.PersonligSimuleringExtendedResultMapperV8.extendedResultV8
import no.nav.pensjon.kalkulator.simulering.api.map.PersonligSimuleringResultMapperV8.resultV8
import no.nav.pensjon.kalkulator.simulering.api.map.PersonligSimuleringSpecMapperV8.fromSpecV8
import no.nav.pensjon.kalkulator.simulering.api.map.PersonligSimuleringExtendedResultMapperV9.extendedResultV9
import no.nav.pensjon.kalkulator.simulering.api.map.PersonligSimuleringResultMapperV9.resultV9
import no.nav.pensjon.kalkulator.simulering.api.map.PersonligSimuleringSpecMapperV9.fromSpecV9
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.BadRequestException
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class SimuleringController(
    private val service: SimuleringService,
    private val feature: FeatureToggleService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v9/alderspensjon/simulering")
    @Operation(
        summary = "Simuler alderspensjon",
        description = "Lag en prognose for framtidig alderspensjon med støtte for AFP i offentlig sektor." +
                " Feltet 'epsHarInntektOver2G' brukes til å angi hvorvidt ektefelle/partner/samboer har inntekt" +
                " over 2 ganger grunnbeløpet. Dersom simulering med de angitte parametre resulterer i avslag i" +
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
                responseCode = "503", description = "Simulering kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun simulerAlderspensjonV9(@RequestBody spec: PersonligSimuleringSpecV9): PersonligSimuleringResultV9 {
        traceAid.begin()
        log.debug { "Request for V9 simulering: $spec" }

        return try {
            if (feature.isEnabled("utvidet-simuleringsresultat"))
                extendedResultV9(
                    timed(
                        function = service::simulerPersonligAlderspensjon,
                        argument = fromSpecV9(spec),
                        functionName = "alderspensjon/simulering"
                    ),
                    spec.foedselsdato
                ).also {
                    log.debug { "Simulering V9 respons: $it" }
                    Metrics.countType(eventName = SIMULERING_TYPE_METRIC_NAME, type = spec.simuleringstype.name)
                }
            else
                resultV9(
                    timed(
                        function = service::simulerPersonligAlderspensjon,
                        argument = fromSpecV9(spec),
                        functionName = "alderspensjon/simulering"
                    ),
                    spec.foedselsdato
                ).also {
                    log.debug { "Simulering V9 respons: $it" }
                    Metrics.countType(eventName = SIMULERING_TYPE_METRIC_NAME, type = spec.simuleringstype.name)
                }
        } catch (e: BadRequestException) {
            badRequest(e)!!
        } catch (e: EgressException) {
            if (e.isConflict) vilkaarIkkeOppfyltV9() else handleError(e, "V9")!!
        } finally {
            traceAid.end()
        }
    }

    @PostMapping("v8/alderspensjon/simulering")
    @Operation(
        summary = "Simuler alderspensjon",
        description = "Lag en prognose for framtidig alderspensjon med støtte for AFP i offentlig sektor." +
                " Feltet 'epsHarInntektOver2G' brukes til å angi hvorvidt ektefelle/partner/samboer har inntekt" +
                " over 2 ganger grunnbeløpet. Dersom simulering med de angitte parametre resulterer i avslag i" +
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
                responseCode = "503", description = "Simulering kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun simulerAlderspensjonV8(@RequestBody spec: PersonligSimuleringSpecV8): PersonligSimuleringResultV8 {
        traceAid.begin()
        log.debug { "Request for V8 simulering: $spec" }

        return try {
            if (feature.isEnabled("utvidet-simuleringsresultat"))
                extendedResultV8(
                    timed(
                        function = service::simulerPersonligAlderspensjon,
                        argument = fromSpecV8(spec),
                        functionName = "alderspensjon/simulering"
                    ),
                    spec.foedselsdato
                ).also {
                    log.debug { "Simulering V8 respons: $it" }
                    Metrics.countType(eventName = SIMULERING_TYPE_METRIC_NAME, type = spec.simuleringstype.name)
                }
            else
                resultV8(
                    timed(
                        function = service::simulerPersonligAlderspensjon,
                        argument = fromSpecV8(spec),
                        functionName = "alderspensjon/simulering"
                    ),
                    spec.foedselsdato
                ).also {
                    log.debug { "Simulering V8 respons: $it" }
                    Metrics.countType(eventName = SIMULERING_TYPE_METRIC_NAME, type = spec.simuleringstype.name)
                }
        } catch (e: BadRequestException) {
            badRequest(e)!!
        } catch (e: EgressException) {
            if (e.isConflict) vilkaarIkkeOppfyltV8() else handleError(e, "V8")!!
        } finally {
            traceAid.end()
        }
    }

    @PostMapping("v1/alderspensjon/anonym-simulering")
    @Operation(
        summary = "Simuler alderspensjon anonymt (ikke innlogget)",
        description = "Lag en prognose for framtidig alderspensjon med støtte for AFP i offentlig sektor." +
                " Feltet 'epsHarInntektOver2G' brukes til å angi hvorvidt ektefelle/partner/samboer har inntekt" +
                " over 2 ganger grunnbeløpet. Dersom simulering med de angitte parametre resulterer i avslag i" +
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
                responseCode = "4xx", description = "Simulering kunne ikke utføres pga. feil i input-data",
                content = [Content(schema = Schema(implementation = AnonymSimuleringErrorV1::class))]
            ),
        ]
    )
    fun simulerAnonymAlderspensjonV1(@RequestBody spec: AnonymSimuleringSpecV1): ResponseEntity<AnonymSimuleringResultV1> {
        traceAid.begin()
        log.debug { "Request for anonym simulering V1: $spec" }

        return try {
            ResponseEntity.ok(
                resultatV1(
                    timed(
                        service::simulerAnonymAlderspensjon,
                        AnonymSimuleringSpecMapperV1.fromAnonymSimuleringSpecV1(spec),
                        "alderspensjon/anonym-simulering"
                    ).also {
                        log.debug { "Anonym simulering V1 respons: $it" }
                        Metrics.countType(eventName = SIMULERING_TYPE_METRIC_NAME, type = "anonym")
                    }
                ))
        } catch (e: SimuleringException) {
            throw e // delegate handling to ExceptionHandler to avoid returning ResponseEntity<Any>
        } catch (e: BadRequestException) {
            badRequest(e)!!
        } catch (e: EgressException) {
            if (e.isConflict && e.errorObj != null) throw e
            else ResponseEntity.badRequest().body(handleError(e, "V1"))
        } finally {
            traceAid.end()
        }
    }

    @ExceptionHandler(EgressException::class)
    fun handleError(e: EgressException): ResponseEntity<AnonymSimuleringErrorV1> =
        ResponseEntity
            .status(e.statusCode ?: HttpStatus.INTERNAL_SERVER_ERROR)
            .body(e.errorObj)

    @ExceptionHandler(SimuleringException::class)
    fun handleError(e: SimuleringException): ResponseEntity<AnonymSimuleringErrorV1> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(errorV1(e))

    override fun errorMessage() = ERROR_MESSAGE

    companion object {
        private const val ERROR_MESSAGE = "feil ved simulering"
        private const val SIMULERING_TYPE_METRIC_NAME = "simulering_type"

        private fun vilkaarIkkeOppfyltV9() =
            PersonligSimuleringResultV9(
                alderspensjon = emptyList(),
                pre2025OffentligAfp = null,
                afpPrivat = null,
                afpOffentlig = null,
                vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV9(
                    vilkaarErOppfylt = false,
                    alternativ = null
                ),
                harForLiteTrygdetid = false
            )

        private fun vilkaarIkkeOppfyltV8() =
            PersonligSimuleringResultV8(
                alderspensjon = emptyList(),
                pre2025OffentligAfp = null,
                afpPrivat = null,
                afpOffentlig = null,
                vilkaarsproeving = PersonligSimuleringVilkaarsproevingResultV8(
                    vilkaarErOppfylt = false,
                    alternativ = null
                ),
                harForLiteTrygdetid = false
            )
    }
}
