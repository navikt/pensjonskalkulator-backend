package no.nav.pensjon.kalkulator.normalder.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.normalder.api.dto.AldersgrenseResultV1
import no.nav.pensjon.kalkulator.normalder.api.dto.AldersgrenseResultV2
import no.nav.pensjon.kalkulator.normalder.api.dto.AldersgrenseSpec
import no.nav.pensjon.kalkulator.normalder.api.map.AldersgrenseMapperV1.dtoV1
import no.nav.pensjon.kalkulator.normalder.api.map.AldersgrenseMapperV2.dto
import no.nav.pensjon.kalkulator.normalder.api.map.AldersgrenseMapperV2.fromDto
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api")
class NormertPensjonsalderController(
    private val service: NormertPensjonsalderService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v1/aldersgrense")
    @Operation(
        summary = "Hent nedre aldersgrense og normert pensjonsalder",
        description = "Henter informasjon om aldersgrensene for et årskull."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av aldersgrenser utført."
            ),
            ApiResponse(
                responseCode = "503",
                description = "Henting av aldersgrenser kunne ikke utføres av tekniske årsaker.",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun aldersgrenserV1(@RequestBody spec: AldersgrenseSpec): AldersgrenseResultV1 {
        traceAid.begin()
        log.debug { "Request for aldersgrense V1" }

        return try {
            dtoV1(timed({ service.aldersgrenser(fromDto(spec)) }, "hentAldersgrenser"))
                .also { log.debug { "aldersgrense respons: $it" } }
        } catch (e: NotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: EgressException) {
            handleError<AldersgrenseResultV1>(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    @PostMapping("v2/aldersgrense")
    @Operation(
        summary = "Hent nedre aldersgrense, øvre aldersgrense og normert pensjonsalder",
        description = "Henter informasjon om aldersgrensene for et årskull."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av aldersgrenser utført."
            ),
            ApiResponse(
                responseCode = "503",
                description = "Henting av aldersgrenser kunne ikke utføres av tekniske årsaker.",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun aldersgrenserV2(@RequestBody spec: AldersgrenseSpec): AldersgrenseResultV2 {
        traceAid.begin()
        log.debug { "Request for aldersgrense V2" }

        return try {
            dto(timed({ service.aldersgrenser(fromDto(spec)) }, "hentAldersgrenser"))
                .also { log.debug { "aldersgrense respons: $it" } }
        } catch (e: NotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: EgressException) {
            handleError<AldersgrenseResultV2>(e, "V2")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av aldersgrense"
    }
}
