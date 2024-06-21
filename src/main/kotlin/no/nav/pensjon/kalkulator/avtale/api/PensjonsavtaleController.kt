package no.nav.pensjon.kalkulator.avtale.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleService
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleSpecV2
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleResultV2
import no.nav.pensjon.kalkulator.avtale.api.map.PensjonsavtaleResultMapperV2.toDtoV2
import no.nav.pensjon.kalkulator.avtale.api.map.PensjonsavtaleSpecMapperV2.fromDtoV2
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class PensjonsavtaleController(
    private val service: PensjonsavtaleService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v2/pensjonsavtaler")
    @Operation(
        summary = "Hent pensjonsavtaler (versjon 2)",
        description = "Henter pensjonsavtalene til den innloggede/angitte brukeren. I request må verdi av 'maaneder' være 0..11."
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
    fun fetchAvtalerV2(@RequestBody spec: PensjonsavtaleSpecV2): PensjonsavtaleResultV2 {
        traceAid.begin()
        val version = "V2"
        log.debug { "Request for pensjonsavtaler $version: $spec" }

        return try {
            toDtoV2(timed(service::fetchAvtaler, fromDtoV2(spec), "pensjonsavtaler $version"))
                .also { log.debug { "Pensjonsavtaler respons $version: $it" } }
        } catch (e: EgressException) {
            handleError(e, version)!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av pensjonsavtaler"
    }
}
