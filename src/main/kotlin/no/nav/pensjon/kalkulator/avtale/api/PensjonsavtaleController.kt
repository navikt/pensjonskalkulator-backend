package no.nav.pensjon.kalkulator.avtale.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleService
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleIngressSpecDto
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleIngressSpecV0Dto
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtalerDto
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtalerV0Dto
import no.nav.pensjon.kalkulator.avtale.api.map.PensjonsavtaleMapper.fromDto
import no.nav.pensjon.kalkulator.avtale.api.map.PensjonsavtaleMapper.toDto
import no.nav.pensjon.kalkulator.avtale.api.map.PensjonsavtaleMapper.toV0Dto
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class PensjonsavtaleController(
    private val service: PensjonsavtaleService,
    private val traceAid: TraceAid
) : ControllerBase() {

    @PostMapping("v1/pensjonsavtaler")
    @Operation(
        summary = "Hent pensjonsavtaler",
        description = "Henter pensjonsavtalene til den innloggede brukeren. I request må verdi av 'maaneder' være 0..11."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av pensjonsavtaler utført. I respons er verdi av 'maaneder' 0..11."
            ),
            ApiResponse(
                responseCode = "503", description = "Henting av pensjonsavtaler kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun fetchAvtalerV1(@RequestBody spec: PensjonsavtaleIngressSpecDto): PensjonsavtalerDto {
        traceAid.initialize()
        log.info { "Request for pensjonsavtaler V1: $spec" }

        return try {
            toDto(timed(service::fetchAvtaler, fromDto(spec), "pensjonsavtaler V1"))
                .also { log.info { "Pensjonsavtaler respons V1: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.finalize()
        }
    }

    @PostMapping("pensjonsavtaler")
    @Operation(
        summary = "Hent pensjonsavtaler – FORELDET; bruk v1/pensjonsavtaler",
        description = "Henter pensjonsavtalene til den innloggede brukeren. I request må verdi av 'startMaaned' være 1..12."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av pensjonsavtaler utført. I respons er verdi av 'startMaaned' og 'sluttMaaned' 1..12."
            ),
            ApiResponse(
                responseCode = "503", description = "Henting av pensjonsavtaler kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun fetchAvtalerV0(@RequestBody spec: PensjonsavtaleIngressSpecV0Dto): PensjonsavtalerV0Dto {
        traceAid.initialize()
        log.info { "Request for pensjonsavtaler V0: $spec" }

        return try {
            toV0Dto(timed(service::fetchAvtaler, fromDto(spec), "pensjonsavtaler V0"))
                .also { log.info { "Pensjonsavtaler respons V0: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V0")!!
        } finally {
            traceAid.finalize()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av pensjonsavtaler"
    }
}
