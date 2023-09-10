package no.nav.pensjon.kalkulator.simulering.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecDto
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringsresultatDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.toDto
import no.nav.pensjon.kalkulator.tech.time.Timed
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api")
class SimuleringController(private val service: SimuleringService) : Timed() {

    @PostMapping("alderspensjon/simulering")
    @Operation(
        summary = "Simuler alderspensjon",
        description = "Lag en prognose for framtidig alderspensjon"
    )
    fun simulerAlderspensjon(@RequestBody spec: SimuleringSpecDto): SimuleringsresultatDto {
        try {
            return toDto(timed(service::simulerAlderspensjon, spec, "alderspensjon/simulering"))
        } catch (e: EgressException) {
            throw ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Feil ved simulering av alderspensjon: ${extractMessageRecursively(e)}",
                e
            )
        }
    }
}
