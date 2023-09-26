package no.nav.pensjon.kalkulator.simulering.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecDto
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecV0Dto
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringsresultatDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.fromSpecDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.fromV0SpecDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.resultatDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.vilkaarsbruddDto
import no.nav.pensjon.kalkulator.tech.time.Timed
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api")
class SimuleringController(
    private val service: SimuleringService,
    private val traceAid: TraceAid
) : Timed() {

    @PostMapping("v1/alderspensjon/simulering")
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
                        " (men dersom vilkår ikke oppfylt vil responsen ikke inneholde pensjonsbeløp).",
                content = [Content(
                    examples = [
                        ExampleObject(name = "Vilkår oppfylt", value = VILKAAR_OPPFYLT_EXAMPLE),
                        ExampleObject(name = "Vilkår ikke oppfylt", value = VILKAAR_IKKE_OPPFYLT_EXAMPLE)]
                )]
            ),
            ApiResponse(
                responseCode = "503", description = "Simulering kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVALABLE_EXAMPLE)])]
            ),
        ]
    )
    fun simulerAlderspensjonV1(@RequestBody spec: SimuleringSpecDto): SimuleringsresultatDto {
        traceAid.initialize()
        log.info { "Request for simulering V1: $spec" }

        return try {
            resultatDto(timed(service::simulerAlderspensjon, fromSpecDto(spec), "alderspensjon/simulering"))
                .also { log.info { "Simulering respons V1: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V1")
        } finally {
            traceAid.finalize()
        }
    }

    @PostMapping("alderspensjon/simulering")
    @Operation(
        summary = "Simuler alderspensjon – FORELDET; bruk v1/alderspensjon/simulering",
        description = "Lag en prognose for framtidig alderspensjon",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Simulering utført (men dersom vilkår ikke oppfylt vil responsen ikke inneholde pensjonsbeløp)",
                content = [Content(
                    examples = [
                        ExampleObject(name = "Vilkår oppfylt", value = VILKAAR_OPPFYLT_EXAMPLE),
                        ExampleObject(name = "Vilkår ikke oppfylt", value = VILKAAR_IKKE_OPPFYLT_EXAMPLE)]
                )]
            ),
            ApiResponse(
                responseCode = "503", description = "Simulering kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVALABLE_EXAMPLE)])]
            ),
        ]
    )
    fun simulerAlderspensjonV0(@RequestBody spec: SimuleringSpecV0Dto): SimuleringsresultatDto {
        traceAid.initialize()
        log.info { "Request for simulering V0: $spec" }

        return try {
            resultatDto(timed(service::simulerAlderspensjon, fromV0SpecDto(spec), "alderspensjon/simulering"))
                .also { log.info { "Simulering respons V0: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V0")
        } finally {
            traceAid.finalize()
        }
    }

    private fun handleError(e: EgressException, version: String) =
        if (e.isClientError)
            handleInternalError(e, version)
        else
            handleExternalError(e, version)

    private fun handleInternalError(e: EgressException, version: String): SimuleringsresultatDto {
        logError(e, "Intern", version)
        return vilkaarsbruddDto()
    }

    private fun handleExternalError(e: EgressException, version: String): SimuleringsresultatDto {
        logError(e, "Ekstern", version)
        return serviceUnavailable(e)
    }

    private fun logError(e: EgressException, category: String, version: String) {
        log.error { "$category feil ved simulering $version: ${extractMessageRecursively(e)}" }
    }

    private fun serviceUnavailable(e: EgressException): SimuleringsresultatDto {
        throw ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Feil ved simulering av alderspensjon: ${extractMessageRecursively(e)}",
            e
        )
    }

    private companion object {

        @Language("json")
        private const val VILKAAR_OPPFYLT_EXAMPLE = """{
  "alderspensjon": [
    {
      "alder": 67,
      "beloep": 300000
    }
  ],
  "afpPrivat": [
    {
      "alder": 62,
      "beloep": 50000
    }
  ],
  "vilkaarErOppfylt": true
}"""

        @Language("json")
        private const val VILKAAR_IKKE_OPPFYLT_EXAMPLE = """{
  "alderspensjon": [],
  "afpPrivat": [],
  "vilkaarErOppfylt": false
}"""

        @Language("json")
        private const val SERVICE_UNAVALABLE_EXAMPLE = """{
    "timestamp": "2023-09-12T10:37:47.056+00:00",
    "status": 503,
    "error": "Service Unavailable",
    "message": "En feil inntraff",
    "path": "/api/v1/alderspensjon/simulering"
}"""
    }
}
