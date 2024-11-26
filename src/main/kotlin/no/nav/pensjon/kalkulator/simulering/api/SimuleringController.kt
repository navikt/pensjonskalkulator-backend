package no.nav.pensjon.kalkulator.simulering.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.simulering.AnonymSimuleringService
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import no.nav.pensjon.kalkulator.simulering.api.map.AnonymSimuleringResultMapperV1.resultatV1
import no.nav.pensjon.kalkulator.simulering.api.map.AnonymSimuleringSpecMapperV1
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringExtendedResultMapperV7.extendedResultV7
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringResultMapperV7.resultatV7
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringSpecMapperV7.fromIngressSimuleringSpecV7
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.BadRequestException
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class SimuleringController(
    private val service: SimuleringService,
    private val anonymService: AnonymSimuleringService,
    private val feature: FeatureToggleService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v7/alderspensjon/simulering")
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
    fun simulerAlderspensjonV7(@RequestBody spec: IngressSimuleringSpecV7): SimuleringResultatV7 {
        traceAid.begin()
        log.debug { "Request for V7 simulering: $spec" }

        return try {
            if (feature.isEnabled("utvidet-simuleringsresultat"))
                extendedResultV7(
                    timed(
                        service::simulerAlderspensjon,
                        fromIngressSimuleringSpecV7(spec),
                        "alderspensjon/simulering"
                    )
                )
                    .also { log.debug { "Simulering V7 respons: $it" } }
            else
                resultatV7(
                    timed(
                        service::simulerAlderspensjon,
                        fromIngressSimuleringSpecV7(spec),
                        "alderspensjon/simulering"
                    )
                )
                    .also { log.debug { "Simulering V7 respons: $it" } }

        } catch (e: BadRequestException) {
            badRequest(e)!!
        } catch (e: EgressException) {
            if (e.isConflict) vilkaarIkkeOppfyltV7() else handleError(e, "V7")!!
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
    fun simulerAnonymAlderspensjonV1(@RequestBody spec: AnonymSimuleringSpecV1): ResponseEntity<AnonymSimuleringResultV1?>? {
        traceAid.begin()
        log.debug { "Request for anonym simulering V1: $spec" }


        return try {
            ResponseEntity.ok(resultatV1(
                timed(
                    anonymService::simulerAlderspensjon,
                    AnonymSimuleringSpecMapperV1.fromAnonymSimuleringSpecV1(spec),
                    "alderspensjon/anonym-simulering"
                )
                    .also { log.debug { "Anonym simulering V1 respons: $it" } }
            ))
        } catch (e: BadRequestException) {
            badRequest(e)!!
        } catch (e: EgressException) {
            if (e.isConflict && e.errorObj != null) throw e
            else ResponseEntity.badRequest().body(handleError(e, "V6"))
        } finally {
            traceAid.end()
        }
    }

    @ExceptionHandler(EgressException::class)
    fun handleError(e: EgressException): ResponseEntity<AnonymSimuleringErrorV1> {
        val status = e.statusCode ?: HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity.status(status).body(e.errorObj)
    }

    override fun errorMessage() = ERROR_MESSAGE

    companion object {
        private const val ERROR_MESSAGE = "feil ved simulering"

        private fun vilkaarIkkeOppfyltV7() =
            SimuleringResultatV7(
                alderspensjon = emptyList(),
                afpPrivat = null,
                afpOffentlig = null,
                vilkaarsproeving = VilkaarsproevingV7(vilkaarErOppfylt = false, alternativ = null),
                harForLiteTrygdetid = false
            )

        @Language("json")
        const val VILKAAR_IKKE_OPPFYLT_EXAMPLE_V7 =
            """{"alderspensjon":[],"vilkaarsproeving":{"vilkaarErOppfylt":false}}"""
    }
}
