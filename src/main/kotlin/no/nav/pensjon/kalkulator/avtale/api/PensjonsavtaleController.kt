package no.nav.pensjon.kalkulator.avtale.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleService
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleSpecDto
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtalerDto
import no.nav.pensjon.kalkulator.avtale.api.map.PensjonsavtaleMapper.toDto
import no.nav.pensjon.kalkulator.tech.time.Timed
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class PensjonsavtaleController(private val service: PensjonsavtaleService) : Timed() {

    @PostMapping("pensjonsavtaler")
    @Operation(
        summary = "Hent pensjonsavtaler",
        description = "Henter pensjonsavtalene til den innloggede brukeren"
    )
    fun fetchAvtaler(@RequestBody spec: PensjonsavtaleSpecDto): PensjonsavtalerDto {
        return toDto(timed(service::fetchAvtaler, spec, "pensjonsavtaler"))
    }
}
