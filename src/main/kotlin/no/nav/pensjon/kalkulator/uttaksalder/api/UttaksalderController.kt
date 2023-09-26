package no.nav.pensjon.kalkulator.uttaksalder.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.tech.time.Timed
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderService
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.AlderDto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderIngressSpecDto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderV0Dto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.map.UttaksalderMapper
import org.intellij.lang.annotations.Language
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
) : Timed() {

    @PostMapping("v1/tidligste-uttaksalder")
    @Operation(
        summary = "Første mulige uttaksalder",
        description = "Finn første mulige uttaksalder for innlogget bruker." +
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
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVALABLE_EXAMPLE)])]
            ),
        ]
    )
    fun finnTidligsteUttaksalderV1(@RequestBody spec: UttaksalderIngressSpecDto?): AlderDto? {
        traceAid.initialize()
        log.info { "Request for uttaksalder-søk V1: $spec" }

        return try {
            toV1Dto(
                timed(
                    service::finnTidligsteUttaksalder,
                    spec ?: UttaksalderIngressSpecDto.empty(),
                    "finnTidligsteUttaksalder"
                )
            )
                .also { log.info { "Uttaksalder-søk respons V1: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V1")
        } finally {
            traceAid.finalize()
        }
    }

    @PostMapping("tidligste-uttaksalder")
    @Operation(
        summary = "Første mulige uttaksalder – FORELDET; bruk v1/tidligste-uttaksalder",
        description = "Finn første mulige uttaksalder for innlogget bruker",
    )
    fun finnTidligsteUttaksalderV0(@RequestBody spec: UttaksalderIngressSpecDto?): UttaksalderV0Dto? {
        traceAid.initialize()
        log.info { "Request for uttaksalder-søk V0: $spec" }

        return try {
            toV0Dto(
                timed(
                    service::finnTidligsteUttaksalder,
                    spec ?: UttaksalderIngressSpecDto.empty(),
                    "finnTidligsteUttaksalder"
                )
            )
                .also { log.info { "Uttaksalder-søk respons V0: $it" } }
        } catch (e: EgressException) {
            //handleError<UttaksalderV0Dto>(e, "V0")
            handleError(e, "V0")
        } finally {
            traceAid.finalize()
        }
    }

    private fun <T> handleError(e: EgressException, version: String) =
        if (e.isClientError) // "client" is here the backend server itself (calling other services)
            handleInternalError<T>(e, version)
        else
            handleExternalError<T>(e, version)

    private fun <T> handleInternalError(e: EgressException, version: String): T? {
        logError(e, "Intern", version)

        throw ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "${ERROR_MESSAGE}: ${extractMessageRecursively(e)}",
            e
        )
    }

    private fun <T> handleExternalError(e: EgressException, version: String): T? {
        logError(e, "Ekstern", version)
        return serviceUnavailable(e)
    }

    private fun logError(e: EgressException, category: String, version: String) {
        log.error { "$category $ERROR_MESSAGE $version: ${extractMessageRecursively(e)}" }
    }

    private fun <T> serviceUnavailable(e: EgressException): T? {
        throw ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE,
            "${ERROR_MESSAGE}: ${extractMessageRecursively(e)}",
            e
        )
    }

    private companion object {
        private const val ERROR_MESSAGE = "feil ved bestemmelse av første mulige uttaksalder"

        private fun toV0Dto(uttaksalder: Alder?): UttaksalderV0Dto? =
            uttaksalder?.let { UttaksalderMapper.toV0Dto(uttaksalder) }

        private fun toV1Dto(uttaksalder: Alder?): AlderDto? =
            uttaksalder?.let { UttaksalderMapper.toV1Dto(uttaksalder) }

        @Language("json")
        private const val SERVICE_UNAVALABLE_EXAMPLE = """{
    "timestamp": "2023-09-12T10:37:47.056+00:00",
    "status": 503,
    "error": "Service Unavailable",
    "message": "En feil inntraff",
    "path": "/api/v1/tidligste-uttaksalder"
}"""
    }
}
