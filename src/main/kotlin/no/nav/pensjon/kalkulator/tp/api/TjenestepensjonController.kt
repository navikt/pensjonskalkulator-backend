package no.nav.pensjon.kalkulator.tp.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.tech.time.Timed
import no.nav.pensjon.kalkulator.tp.TjenestepensjonService
import no.nav.pensjon.kalkulator.tp.api.dto.TjenestepensjonsforholdDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class TjenestepensjonController(private val service: TjenestepensjonService) : Timed() {

    @GetMapping("tpo-medlemskap")
    @Operation(
        summary = "Har offentlig tjenestepensjonsforhold",
        description = "Hvorvidt den innloggede brukeren har offentlig tjenestepensjonsforhold"
    )
    fun harTjenestepensjonsforhold(): TjenestepensjonsforholdDto {
        return toDto(timed(service::harTjenestepensjonsforhold, "harTjenestepensjonsforhold"))
    }

    private companion object {
        private fun toDto(harForhold: Boolean) = TjenestepensjonsforholdDto(harForhold)
    }
}
