package no.nav.pensjon.kalkulator.ekskludering.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingFacade
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingStatusV1
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingStatusV2
import no.nav.pensjon.kalkulator.ekskludering.api.map.EkskluderingMapper.version1
import no.nav.pensjon.kalkulator.ekskludering.api.map.EkskluderingMapper.version2
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class EkskluderingController(
    private val service: EkskluderingFacade,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @GetMapping("v1/ekskludert")
    @Operation(
        summary = "Om personen er ekskludert fra å bruke kalkulatoren",
        description = "Eksludering kan skyldes løpende uføretrygd, gjenlevendeytelse eller medlemskap i Apotekerforeningen"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Sjekking av ekskludering utført"
            ),
            ApiResponse(
                responseCode = "503", description = "Sjekking av ekskludering kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun erEkskludertV1(): EkskluderingStatusV1 {
        traceAid.begin()
        log.debug { "Request for ekskludering-status" }

        return try {
            version1(timed(service::erEkskludert, "erEkskludertV1"))
                .also { log.debug { "Eksludering-status respons: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    @GetMapping("v2/ekskludert")
    @Operation(
        summary = "Om personen er ekskludert fra å bruke kalkulatoren",
        description = "Eksludering kan skyldes medlemskap i Apotekerforeningen"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Sjekking av ekskludering utført"
            ),
            ApiResponse(
                responseCode = "503", description = "Sjekking av ekskludering kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun erEkskludertV2(): EkskluderingStatusV2 {
        traceAid.begin()
        log.debug { "Request for ekskludering-status" }

        return try {
            version2(timed(service::erEkskludertV2, "erEkskludertV2"))
                .also { log.debug { "Eksludering-status respons: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V2")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "Feil ved sjekking av ekskludering"
    }
}
