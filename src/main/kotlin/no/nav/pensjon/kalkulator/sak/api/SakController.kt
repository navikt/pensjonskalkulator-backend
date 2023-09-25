package no.nav.pensjon.kalkulator.sak.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.sak.SakService
import no.nav.pensjon.kalkulator.sak.api.dto.SakDto
import no.nav.pensjon.kalkulator.tech.time.Timed
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api")
class SakController(private val service: SakService) : Timed() {

    @GetMapping("sak-status")
    @Operation(
        summary = "Har uføretrygd/gjenlevendeytelse",
        description = "Hvorvidt den innloggede brukeren har løpende uføretrygd eller gjenlevendeytelse"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Sjekking av saker utført"
            ),
            ApiResponse(
                responseCode = "503", description = "Sjekking av saker kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVALABLE_EXAMPLE)])]
            ),
        ]
    )
    fun harRelevantSak(): SakDto =
        try {
            toDto(timed(service::harRelevantSak, "harRelevantSak"))
        } catch (e: EgressException) {
            if (e.isClientError) clientError(e) else serviceUnavailable(e)
        }

    // The "client" is in this case the backend server itself (calling other back services)
    private fun clientError(e: EgressException): SakDto {
        throw ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "$ERROR_MESSAGE: ${extractMessageRecursively(e)}",
            e
        )
    }

    private fun serviceUnavailable(e: EgressException): SakDto {
        throw ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE,
            "$ERROR_MESSAGE: ${extractMessageRecursively(e)}",
            e
        )
    }

    private companion object {
        private const val ERROR_MESSAGE = "Feil ved sjekking av saker"

        private fun toDto(harSak: Boolean) = SakDto(harSak)

        @Language("json")
        private const val SERVICE_UNAVALABLE_EXAMPLE = """{
    "timestamp": "2023-09-12T10:37:47.056+00:00",
    "status": 503,
    "error": "Service Unavailable",
    "message": "En feil inntraff",
    "path": "/api/sak-status"
}"""
    }
}
