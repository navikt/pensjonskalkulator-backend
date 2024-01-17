package no.nav.pensjon.kalkulator.uttaksalder.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderService
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.AlderDto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderIngressSpecDto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderIngressSpecDtoV2
import no.nav.pensjon.kalkulator.uttaksalder.api.map.UttaksalderMapper.fromIngressSpecDto
import no.nav.pensjon.kalkulator.uttaksalder.api.map.UttaksalderMapper.fromIngressSpecDtoV2
import no.nav.pensjon.kalkulator.uttaksalder.api.map.UttaksalderMapper.toDto
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

    @PostMapping("v1/tidligste-uttaksalder")
    @Operation(
        summary = "Første mulige uttaksalder (uten inntekt under helt uttak)",
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
    fun finnTidligsteUttaksalder(@RequestBody spec: UttaksalderIngressSpecDto): AlderDto? {
        traceAid.begin()
        log.debug { "Request for uttaksalder-søk V1: $spec" }

        return try {
            toDto(
                timed(
                    service::finnTidligsteUttaksalder,
                    fromIngressSpecDto(spec),
                    "finnTidligsteUttaksalder"
                )
            )
                .also { log.debug { "Uttaksalder-søk respons V1: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V1")
        } finally {
            traceAid.end()
        }
    }

    @PostMapping("v2/tidligste-uttaksalder")
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
    fun finnTidligsteUttaksalderV2(@RequestBody spec: UttaksalderIngressSpecDtoV2): AlderDto? {
        traceAid.begin()
        log.debug { "Request for uttaksalder-søk V2: $spec" }

        return try {
            toDto(
                timed(
                    service::finnTidligsteUttaksalder,
                    fromIngressSpecDtoV2(spec),
                    "finnTidligsteUttaksalder"
                )
            )
                .also { log.debug { "Uttaksalder-søk respons V2: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V2")
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved bestemmelse av første mulige uttaksalder"
    }
}
