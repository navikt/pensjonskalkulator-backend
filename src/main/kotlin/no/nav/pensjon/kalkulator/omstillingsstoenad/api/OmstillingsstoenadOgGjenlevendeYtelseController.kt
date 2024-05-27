package no.nav.pensjon.kalkulator.omstillingsstoenad.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingOgGjenlevendeYtelseService
import no.nav.pensjon.kalkulator.omstillingsstoenad.api.dto.BrukerHarLoependeOmstillingsstoenadEllerGjenlevendeYtelse
import no.nav.pensjon.kalkulator.omstillingsstoenad.api.map.OmstillingsstoenadMapper.toDto
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class OmstillingsstoenadOgGjenlevendeYtelseController(
    private val service: OmstillingOgGjenlevendeYtelseService,
    private val traceAid: TraceAid,
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @GetMapping("/v1/loepende-omstillingsstoenad-eller-gjenlevendeytelse")
    @Operation(
        summary = "Mottar omstillingsstønad eller gjenlevende ytelse",
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
    suspend fun mottarOmstillingsstoenadEllerGjenlevendeYtelse() : BrukerHarLoependeOmstillingsstoenadEllerGjenlevendeYtelse {
        traceAid.begin()
        log.debug { "Request for mottarOmstillingsstoenadEllerGjenlevendeYtelse" }
        return try {
            toDto(timed(service::harLoependeSaker, "mottarOmstillingsstoenadEllerGjenlevendeYtelse"))
                .also { log.debug { "mottarOmstillingsstoenadEllerGjenlevendeYtelse respons: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av omstillingsstønad og gjenlevende ytelse"
    }

}