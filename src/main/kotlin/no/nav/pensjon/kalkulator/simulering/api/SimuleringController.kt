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
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapperV6.fromIngressSimuleringSpecV6
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapperV6.resultatV6
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.BadRequestException
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

    @PostMapping("v6/alderspensjon/simulering")
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
    fun simulerAlderspensjonV6(@RequestBody spec: IngressSimuleringSpecV6): SimuleringResultatV6 {
        traceAid.begin()
        log.debug { "Request for V6 simulering: $spec" }

        return try {
            resultatV6(
                timed(
                    service::simulerAlderspensjon,
                    fromIngressSimuleringSpecV6(spec),
                    "alderspensjon/simulering"
                )
            )
                .also { log.debug { "Simulering V6 respons: $it" } }
        } catch (e: BadRequestException) {
            badRequest(e)!!
        } catch (e: EgressException) {
            if (e.isConflict) vilkaarIkkeOppfyltV6() else handleError(e, "V6")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    companion object {
        private const val ERROR_MESSAGE = "feil ved simulering"

        private fun vilkaarIkkeOppfyltV6() =
            SimuleringResultatV6(
                alderspensjon = emptyList(),
                afpPrivat = null,
                afpOffentlig = null,
                vilkaarsproeving = VilkaarsproevingV6(vilkaarErOppfylt = false, alternativ = null)
            )

        @Language("json")
        const val VILKAAR_IKKE_OPPFYLT_EXAMPLE_V6 = """{"alderspensjon":[],"vilkaarsproeving":{"vilkaarErOppfylt":false}}"""
    }
}
