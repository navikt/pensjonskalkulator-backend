package no.nav.pensjon.kalkulator.uttaksalder.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderService
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.AlderDto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderIngressSpecDto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderV0Dto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.map.UttaksalderMapper.toV0Dto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.map.UttaksalderMapper.toV1Dto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class UttaksalderController(
    private val service: UttaksalderService,
    private val traceAid: TraceAid
) : ControllerBase() {

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
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
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
            handleError(e, "V0")
        } finally {
            traceAid.finalize()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved bestemmelse av første mulige uttaksalder"
    }
}
