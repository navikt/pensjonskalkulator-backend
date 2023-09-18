package no.nav.pensjon.kalkulator.opptjening.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.opptjening.Inntekt
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
        description = "Henter den innloggede brukerens sist skattelignede pensjonsgivende inntekt"
    )
    fun sistePensjonsgivendeInntekt() =
        toDto(timed(service::sistePensjonsgivendeInntekt, "sistePensjonsgivendeInntekt"))

    private companion object {
        private fun toDto(inntekt: Inntekt) = InntektDto(inntekt.beloep.intValueExact(), inntekt.aar)
    }
}
