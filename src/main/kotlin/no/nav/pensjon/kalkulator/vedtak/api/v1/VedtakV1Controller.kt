package no.nav.pensjon.kalkulator.vedtak.api.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.vedtak.VedtakMedUtbetalingService
import no.nav.pensjon.kalkulator.vedtak.api.v1.acl.VedtakResultMapper
import no.nav.pensjon.kalkulator.vedtak.api.v1.acl.VedtakV1Samling
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1")
class VedtakV1Controller(
    val traceAid: TraceAid,
    val service: VedtakMedUtbetalingService
) : ControllerBase(traceAid) {

    @GetMapping("vedtak")
    @Operation(
        summary = "Hent vedtak",
        description = "Henter løpende vedtak om alderspensjon, AFP (i privat og offentlig sektor) og uføretrygd, samt fremtidige alderspensjonsvedtak"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av vedtak utført."
            ),
            ApiResponse(
                responseCode = "400",
                description = "Tjenesten kunne ikke utføres pga. uakseptable inndata."
            ),
            ApiResponse(
                responseCode = "401",
                description = "Tjenesten kunne ikke utføres pga. manglende/feilaktig autentisering."
            ),
            ApiResponse(
                responseCode = "403",
                description = "Tjenesten kunne ikke utføres pga. manglende tilgang til tjenesten."
            ),
            ApiResponse(
                responseCode = "404",
                description = "Tjenesten kunne ikke utføres fordi angitt person ikke finnes i systemet."
            ),
            ApiResponse(
                responseCode = "500",
                description = "Tjenesten kunne ikke utføres pga. feil i systemet."
            )
        ]
    )
    suspend fun hentVedtak(): VedtakV1Samling {
        traceAid.begin()

        return try {
            VedtakResultMapper.toDto(service.hentVedtakMedUtbetaling())
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "Feil ved henting av vedtak"
    }
}
