package no.nav.pensjon.kalkulator.vedtak.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.vedtak.api.dto.LoependeVedtakDetaljerDto
import no.nav.pensjon.kalkulator.vedtak.api.dto.LoependeVedtakDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class VedtakController(private val traceAid: TraceAid) : ControllerBase(traceAid) {

    @GetMapping("/v1/vedtak/loepende-vedtak")
    @Operation(
        summary = "Har løpende saker",
        description = "Hvorvidt den innloggede brukeren har løpende uføretrygd med uttaksgrad, alderspensjon med uttaksgrad, AFP privat eller offentlig"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Sjekking av saker utført"
            ),
            ApiResponse(
                responseCode = "503", description = "Sjekking av saker kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun hentLoependeVedtak(): LoependeVedtakDto {
        return LoependeVedtakDto(
            alderspensjon = LoependeVedtakDetaljerDto(loepende = true, grad = 60),
            ufoeretrygd = LoependeVedtakDetaljerDto(loepende = true, 40),
            afpPrivat = LoependeVedtakDetaljerDto(loepende = true),
            afpOffentlig = LoependeVedtakDetaljerDto()
        )
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "Feil ved sjekking av løpende saker"
    }

}