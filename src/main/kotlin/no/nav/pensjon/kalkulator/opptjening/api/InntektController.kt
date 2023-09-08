package no.nav.pensjon.kalkulator.opptjening.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.opptjening.api.dto.InntektDto
import no.nav.pensjon.kalkulator.tech.time.Timed
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class InntektController(private val service: InntektService) : Timed() {

    @GetMapping("inntekt")
    @Operation(
        summary = "Siste pensjonsgivende inntekt",
        description = "Henter den innloggede brukerens siste pensjonsgivende inntekt"
    )
    fun sistePensjonsgivendeInntekt() =
        toDto(timed(service::sistePensjonsgivendeInntekt, "sistePensjonsgivendeInntekt"))

    private companion object {
        private fun toDto(beloep: Int) = InntektDto(beloep)
    }
}
