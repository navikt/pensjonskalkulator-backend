package no.nav.pensjon.kalkulator.simulering.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecDto
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringsresultatDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.toDto
import no.nav.pensjon.kalkulator.tech.time.Timed
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class SimuleringController(private val simuleringService: SimuleringService) : Timed() {

    @PostMapping("alderspensjon/simulering")
    @Operation(
        summary = "Simuler alderspensjon",
        description = "Lag en prognose for framtidig alderspensjon"
    )
    fun simulerAlderspensjon(@RequestBody spec: SimuleringSpecDto): SimuleringsresultatDto {
        return toDto(timed(simuleringService::simulerAlderspensjon, spec, "alderspensjon/simulering"))
    }
}
