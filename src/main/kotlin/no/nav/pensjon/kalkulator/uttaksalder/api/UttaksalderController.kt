package no.nav.pensjon.kalkulator.uttaksalder.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.tech.time.Timed
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.general.Alder
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
class UttaksalderController(private val service: UttaksalderService) : Timed() {

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
        log.info { "Request for uttaksalder-søk V1: $spec" }

        return try {
            toV1Dto(
                timed(
                    service::finnTidligsteUttaksalder,
                    spec ?: UttaksalderIngressSpecDto.empty(),
                    "finnTidligsteUttaksalder"
                )
            )
        } catch (e: EgressException) {
            log.error { "Feil ved uttaksalder-søk V1: ${extractMessageRecursively(e)}" }
            if (e.isClientError) clientError(e) else serviceUnavailable(e)
        }
    }

    @PostMapping("tidligste-uttaksalder")
    @Operation(
        summary = "Første mulige uttaksalder – FORELDET; bruk v1/tidligste-uttaksalder",
        description = "Finn første mulige uttaksalder for innlogget bruker",
    )
    fun finnTidligsteUttaksalderV0(@RequestBody spec: UttaksalderIngressSpecDto?): UttaksalderV0Dto? {
        log.info { "Request for uttaksalder-søk V0: $spec" }

        try {
            return toV0Dto(
                timed(
                    service::finnTidligsteUttaksalder,
                    spec ?: UttaksalderIngressSpecDto.empty(),
                    "finnTidligsteUttaksalder"
                )
            )
        } catch (e: EgressException) {
            log.error { "Feil ved uttaksalder-søk V0: ${extractMessageRecursively(e)}" }
            throw ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Feil ved bestemmelse av første mulige uttaksalder: ${extractMessageRecursively(e)}",
                e
            )
        }
    }


    // The "client" is in this case the backend server itself (calling other back services)
    private fun clientError(e: EgressException): AlderDto? {
        throw ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "${ERROR_MESSAGE}: ${extractMessageRecursively(e)}",
            e
        )
    }

    private fun serviceUnavailable(e: EgressException): AlderDto? {
        throw ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE,
            "${ERROR_MESSAGE}: ${extractMessageRecursively(e)}",
            e
        )
    }

    private companion object {
        private const val ERROR_MESSAGE = "Feil ved bestemmelse av første mulige uttaksalder"

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
