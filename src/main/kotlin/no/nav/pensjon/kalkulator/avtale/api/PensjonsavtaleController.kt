package no.nav.pensjon.kalkulator.avtale.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleService
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleSpecDto
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtalerDto
import no.nav.pensjon.kalkulator.avtale.api.map.PensjonsavtaleMapper.toDto
import no.nav.pensjon.kalkulator.avtale.client.np.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.tech.time.Timed
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class PensjonsavtaleController(private val service: PensjonsavtaleService) : Timed() {

    @GetMapping("pensjonsavtaler")
    @Operation(
        summary = "Hent pensjonsavtaler",
        description = "Henter pensjonsavtalene til den innloggede brukeren"
    )
    fun getAvtaler(): PensjonsavtalerDto {
        val spec = PensjonsavtaleSpecDto(0, UttaksperiodeSpec(67, 1, 100, 0), 0)
        //TODO get spec from frontend
        return toDto(timed(service::fetchAvtaler, spec, "pensjonsavtaler"))
    }
}
