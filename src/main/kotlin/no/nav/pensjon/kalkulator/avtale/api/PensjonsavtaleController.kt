package no.nav.pensjon.kalkulator.avtale.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleService
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleIngressSpecDto
import no.nav.pensjon.kalkulator.avtale.api.dto.IngressPensjonsavtaleSpecV2
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtalerDto
import no.nav.pensjon.kalkulator.avtale.api.map.PensjonsavtaleMapper.fromDto
import no.nav.pensjon.kalkulator.avtale.api.map.PensjonsavtaleMapper.fromDtoV2
import no.nav.pensjon.kalkulator.avtale.api.map.PensjonsavtaleMapper.toDto
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.json.ObjectMapperConfiguration
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class PensjonsavtaleController(
    private val service: PensjonsavtaleService,
    private val traceAid: TraceAid,
    private val tempPidGetter: PidGetter? = null
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v1/pensjonsavtaler")
    @Operation(
        summary = "Hent pensjonsavtaler (versjon 1)",
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
    fun fetchAvtaler(@RequestBody spec: PensjonsavtaleIngressSpecDto): PensjonsavtalerDto {
        traceAid.begin()
        log.debug { "Request for pensjonsavtaler V1: $spec" }
        val mockFnr = tempPidGetter?.pid()?.value

        return try {
            if ("10836397849" == mockFnr) mockAvtaler() else

            toDto(timed(service::fetchAvtaler, fromDto(spec), "pensjonsavtaler V1"))
                .also { log.debug { "Pensjonsavtaler respons V1: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    @PostMapping("v2/pensjonsavtaler")
    @Operation(
        summary = "Hent pensjonsavtaler (versjon 2)",
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
    fun fetchAvtalerV2(@RequestBody spec: IngressPensjonsavtaleSpecV2): PensjonsavtalerDto {
        traceAid.begin()
        val version = "V2"
        log.debug { "Request for pensjonsavtaler $version: $spec" }
        val mockFnr = tempPidGetter?.pid()?.value

        return try {
            if ("10836397849" == mockFnr) mockAvtaler() else

            toDto(timed(service::fetchAvtaler, fromDtoV2(spec), "pensjonsavtaler $version"))
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

        /**
         * Temporary function for testing many pensjonsavtaler
         */
        private fun mockAvtaler(): PensjonsavtalerDto =
             ObjectMapperConfiguration().objectMapper().readValue(
                """{
  "avtaler": [],
	"utilgjengeligeSelskap": [{
		"navn": "Perpetual Income & Growth Investment Trust PLC",
		"heltUtilgjengelig": false
	}, {
		"navn": "Wüstenrot & Württembergische",
		"heltUtilgjengelig": true
	}, {
		"navn": "UnipolSai (or UnipolSai Assicurazioni) post raggruppamento",
		"heltUtilgjengelig": false
	}, {
		"navn": "Storebrand",
		"heltUtilgjengelig": false
	}, {
		"navn": "Gabler",
		"heltUtilgjengelig": true
	}]
}""", PensjonsavtalerDto::class.java
            )
    }
}
