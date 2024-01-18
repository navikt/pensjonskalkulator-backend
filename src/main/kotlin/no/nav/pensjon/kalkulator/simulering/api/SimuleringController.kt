package no.nav.pensjon.kalkulator.simulering.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.api.dto.IngressSimuleringSpecV2
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecDto
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringsresultatDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.fromIngressSpecDtoV2
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.fromSpecDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.resultatDto
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class SimuleringController(
    private val service: SimuleringService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v1/alderspensjon/simulering")
    @Operation(
        summary = "Simuler hel alderspensjon",
        description = "Lag en prognose for framtidig alderspensjon med 100 % uttak." +
                " Feltet 'epsHarInntektOver2G' brukes til å angi om ektefelle/partner/samboer har inntekt" +
                " over 2 ganger grunnbeløpet eller ei.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Simulering utført" +
                        " (men dersom vilkår ikke oppfylt vil responsen ikke inneholde pensjonsbeløp)."
            ),
            ApiResponse(
                responseCode = "503", description = "Simulering kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun simulerAlderspensjonV1(@RequestBody spec: SimuleringSpecDto): SimuleringsresultatDto {
        traceAid.begin()
        log.debug { "Request for V1 simulering: $spec" }

        return try {
            resultatDto(timed(service::simulerAlderspensjon, fromSpecDto(spec), "alderspensjon/simulering"))
                .also { log.debug { "Simulering V1 respons: $it" } }
        } catch (e: EgressException) {
            if (e.isConflict) vilkaarIkkeOppfylt() else handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    @PostMapping("v2/alderspensjon/simulering")
    @Operation(
        summary = "Simuler alderspensjon",
        description = "Lag en prognose for framtidig alderspensjon." +
                " Feltet 'epsHarInntektOver2G' brukes til å angi om ektefelle/partner/samboer har inntekt" +
                " over 2 ganger grunnbeløpet eller ei.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Simulering utført" +
                        " (men dersom vilkår ikke oppfylt vil responsen ikke inneholde pensjonsbeløp)."
            ),
            ApiResponse(
                responseCode = "503", description = "Simulering kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun simulerAlderspensjonV2(@RequestBody spec: IngressSimuleringSpecV2): SimuleringsresultatDto {
        traceAid.begin()
        log.debug { "Request for simulering: $spec" }

        return try {
            resultatDto(timed(service::simulerAlderspensjon, fromIngressSpecDtoV2(spec), "alderspensjon/simulering"))
                .also { log.debug { "Simulering respons: $it" } }
        } catch (e: EgressException) {
            if (e.isConflict) vilkaarIkkeOppfylt() else handleError(e, "V2")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    companion object {
        private const val ERROR_MESSAGE = "feil ved simulering"

        private fun vilkaarIkkeOppfylt() =
            SimuleringsresultatDto(
                alderspensjon = emptyList(),
                afpPrivat = emptyList(),
                vilkaarErOppfylt = false
            )

        @Language("json")
        const val VILKAAR_IKKE_OPPFYLT_EXAMPLE = """{
  "alderspensjon": [],
  "afpPrivat": [],
  "vilkaarErOppfylt": false
}"""
    }
}
