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
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.AlderDto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.IngressUttaksalderSpecForGradertUttakV1
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.IngressUttaksalderSpecForHeltUttakV1
import no.nav.pensjon.kalkulator.uttaksalder.api.map.UttaksalderMapperV1.fromIngressSpecForGradertUttakV1
import no.nav.pensjon.kalkulator.uttaksalder.api.map.UttaksalderMapperV1.fromIngressSpecForHeltUttakV1
import no.nav.pensjon.kalkulator.uttaksalder.api.map.UttaksalderMapperV1.toDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api")
class UttaksalderController(
    private val service: UttaksalderService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v1/tidligste-hel-uttaksalder")
    @Operation(
        summary = "Første mulige uttaksalder ved helt uttak",
        description = "Finn første mulige uttaksalder for innlogget bruker ved helt (100 %) uttak." +
                " Feltet 'harEps' brukes til å angi om brukeren har ektefelle/partner/samboer eller ei",
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
    fun finnTidligsteHelUttaksalderV1(@RequestBody spec: IngressUttaksalderSpecForHeltUttakV1): AlderDto? {
        traceAid.begin()
        val version = "V1"
        log.debug { "Request for hel uttaksalder-søk $version: $spec" }

        return try {
            toDto(
                timed(
                    service::finnTidligsteUttaksalder,
                    fromIngressSpecForHeltUttakV1(spec),
                    "finnTidligsteHelUttaksalderV1"
                )
            )
                .also { log.debug { "Hel uttaksalder-søk respons $version: $it" } }
        } catch (e: EgressException) {
            handleError(e, version)
        } catch (e: BadRequestException) {
            badRequest(e)!!
        } finally {
            traceAid.end()
        }
    }

    @PostMapping("v1/tidligste-gradert-uttaksalder")
    @Operation(
        summary = "Første mulige uttaksalder ved gradert uttak",
        description = "Finn første mulige uttaksalder for innlogget bruker ved gradert (20–80 %) uttak." +
                " Feltet 'harEps' brukes til å angi om brukeren har ektefelle/partner/samboer eller ei",
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
    fun finnTidligsteGradertUttaksalderV1(@RequestBody spec: IngressUttaksalderSpecForGradertUttakV1): AlderDto? {
        traceAid.begin()
        val version = "V1"
        log.debug { "Request for gradert uttaksalder-søk $version: $spec" }

        return try {
            toDto(
                timed(
                    service::finnTidligsteUttaksalder,
                    fromIngressSpecForGradertUttakV1(spec),
                    "finnTidligsteGradertUttaksalderV1"
                )
            )
                .also { log.debug { "Gradert uttaksalder-søk respons $version: $it" } }
        } catch (e: IllegalArgumentException) {
            handle(e)
        } catch (e: BadRequestException) {
            badRequest(e)!!
        } catch (e: EgressException) {
            handleError(e, version)
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private fun handle(e: IllegalArgumentException): AlderDto? {
        throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Call ID: ${traceAid.callId()} | Error: ${errorMessage()} | Details: ${e.message}",
            e
        )
    }

    private companion object {
        private const val ERROR_MESSAGE = "feil ved bestemmelse av første mulige uttaksalder"
    }
}
