package no.nav.pensjon.kalkulator.uttaksalder.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.BadRequestException
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderService
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderResultV2
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderResultV3
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderSpecV2
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderSpecV3
import no.nav.pensjon.kalkulator.uttaksalder.api.map.UttaksalderResultMapperV2.resultV2
import no.nav.pensjon.kalkulator.uttaksalder.api.map.UttaksalderResultMapperV3.resultV3
import no.nav.pensjon.kalkulator.uttaksalder.api.map.UttaksalderSpecMapperV2.fromDtoV2
import no.nav.pensjon.kalkulator.uttaksalder.api.map.UttaksalderSpecMapperV3.fromDtoV3
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class UttaksalderController(
    private val service: UttaksalderService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v3/tidligste-hel-uttaksalder")
    @Operation(
        summary = "Tidligst mulige uttaksalder ved helt uttak",
        description = "Finn tidligst mulige uttaksalder for innlogget bruker ved helt (100 %) uttak.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Søk etter uttaksalder utført. I resultatet er verdi av 'maaneder' 0..11."
            ),
            ApiResponse(
                responseCode = "503", description = "Søk etter uttaksalder kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun finnTidligsteHelUttaksalderV3(@RequestBody spec: UttaksalderSpecV3): UttaksalderResultV3? {
        traceAid.begin()
        val version = "V3"
        log.debug { "Request for hel uttaksalder-søk $version: $spec" }

        return try {
            resultV3(
                timed(
                    function = service::finnTidligsteUttaksalder,
                    argument = fromDtoV3(spec),
                    functionName = "TMU $version"
                )
            ).also { log.debug { "Hel uttaksalder-søk respons $version: $it" } }
        } catch (e: EgressException) {
            handleError(e, version)
        } catch (e: BadRequestException) {
            badRequest(e)!!
        } finally {
            traceAid.end()
        }
    }

    @PostMapping("v2/tidligste-hel-uttaksalder")
    @Operation(
        summary = "Tidligst mulige uttaksalder ved helt uttak",
        description = "Finn tidligst mulige uttaksalder for innlogget bruker ved helt (100 %) uttak.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Søk etter uttaksalder utført. I resultatet er verdi av 'maaneder' 0..11."
            ),
            ApiResponse(
                responseCode = "503", description = "Søk etter uttaksalder kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun finnTidligsteHelUttaksalderV2(@RequestBody spec: UttaksalderSpecV2): UttaksalderResultV2? {
        traceAid.begin()
        val version = "V2"
        log.debug { "Request for hel uttaksalder-søk $version: $spec" }

        return try {
            resultV2(
                timed(
                    function = service::finnTidligsteUttaksalder,
                    argument = fromDtoV2(spec),
                    functionName = "TMU $version"
                )
            ).also { log.debug { "Hel uttaksalder-søk respons $version: $it" } }
        } catch (e: EgressException) {
            handleError(e, version)
        } catch (e: BadRequestException) {
            badRequest(e)!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved bestemmelse av tidligst mulige uttaksalder"
    }
}
