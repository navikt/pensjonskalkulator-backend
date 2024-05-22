package no.nav.pensjon.kalkulator.omstillingsstoenad.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingsstoenadService
import no.nav.pensjon.kalkulator.omstillingsstoenad.api.dto.BrukerMottarOmstillingsstoenad
import no.nav.pensjon.kalkulator.omstillingsstoenad.api.map.OmstillingsstoenadMapper.toDto
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class OmstillingsstoenadController(
    private val service: OmstillingsstoenadService,
    private val traceAid: TraceAid,
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @GetMapping("/v1/mottar-omstillingsstoenad")
    @Operation(
        summary = "Mottar omstillingsstønad",
        description = "Hvorvidt den innloggede brukeren mottar omstillingsstønad"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av omstillingsstønad utført."
            ),
            ApiResponse(
                responseCode = "503", description = "Henting av omstillingsstønad kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun mottarOmstillingsstoenad(): BrukerMottarOmstillingsstoenad {
        traceAid.begin()
        log.debug { "Request for omstillingsstønad" }
        return try {
            toDto(timed(service::mottarOmstillingsstoenad, "mottarOmstillingsstoenad"))
                .also { log.debug { "Omstillingsstønad respons: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av omstillingsstønad"
    }

}