package no.nav.pensjon.kalkulator.simulering.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapperV3.fromIngressSimuleringSpecV3
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapperV3.resultatV3
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapperV4.fromIngressSimuleringSpecV4
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapperV4.resultatV4
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class SimuleringController(
    private val service: SimuleringService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v4/alderspensjon/simulering")
    @Operation(
        summary = "Simuler alderspensjon",
        description = "Lag en prognose for framtidig alderspensjon med støtte til Afp Offentlig." +
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
    fun simulerAlderspensjonV4(@RequestBody spec: IngressSimuleringSpecV4): SimuleringResultatV4 {
        traceAid.begin()
        log.debug { "Request for V4 simulering: $spec" }

        return try {
            resultatV4(
                timed(
                    service::simulerAlderspensjon,
                    fromIngressSimuleringSpecV4(spec),
                    "alderspensjon/simulering"
                )
            )
                .also { log.debug { "Simulering V4 respons: $it" } }
        } catch (e: EgressException) {
            if (e.isConflict) vilkaarIkkeOppfyltV4() else handleError(e, "V4")!!
        } finally {
            traceAid.end()
        }
    }


    @PostMapping("v3/alderspensjon/simulering")
    @Operation(
        summary = "Simuler alderspensjon",
        description = "Lag en prognose for framtidig alderspensjon." +
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
    fun simulerAlderspensjonV3(@RequestBody spec: IngressSimuleringSpecV3): SimuleringResultatV3 {
        traceAid.begin()
        log.debug { "Request for V3 simulering: $spec" }

        return try {
            resultatV3(
                timed(
                    service::simulerAlderspensjon,
                    fromIngressSimuleringSpecV3(spec),
                    "alderspensjon/simulering"
                )
            )
                .also { log.debug { "Simulering V3 respons: $it" } }
        } catch (e: EgressException) {
            if (e.isConflict) vilkaarIkkeOppfyltV3() else handleError(e, "V3")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    companion object {
        private const val ERROR_MESSAGE = "feil ved simulering"

        private fun vilkaarIkkeOppfyltV4() =
            SimuleringResultatV4(
                alderspensjon = emptyList(),
                afpPrivat = emptyList(),
                afpOffentlig = null,
                vilkaarsproeving = VilkaarsproevingV4(vilkaarErOppfylt = false, alternativ = null)
            )

        private fun vilkaarIkkeOppfyltV3() =
            SimuleringResultatV3(
                alderspensjon = emptyList(),
                afpPrivat = emptyList(),
                vilkaarsproeving = VilkaarsproevingV3(vilkaarErOppfylt = false, alternativ = null)
            )

        @Language("json")
        const val VILKAAR_IKKE_OPPFYLT_EXAMPLE = """{"alderspensjon":[],"afpPrivat":[],"afpOffentlig":null,"vilkaarsproeving":{"vilkaarErOppfylt":false,"alternativ":null}}"""
    }
}
