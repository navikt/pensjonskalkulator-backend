package no.nav.pensjon.kalkulator.simulering.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecDto
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringsresultatDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.resultatDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.vilkaarsbruddDto
import no.nav.pensjon.kalkulator.tech.time.Timed
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api")
class SimuleringController(private val service: SimuleringService) : Timed() {

    @PostMapping("alderspensjon/simulering")
    @Operation(
        summary = "Simuler alderspensjon",
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
    fun simulerAlderspensjon(@RequestBody spec: SimuleringSpecDto): SimuleringsresultatDto {
        return try {
            resultatDto(timed(service::simulerAlderspensjon, spec, "alderspensjon/simulering"))
        } catch (e: EgressException) {
            if (e.isClientError) vilkaarsbruddDto() else serviceUnavailable(e)
        }
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
    "path": "/api/alderspensjon/simulering"
}"""
    }
}
