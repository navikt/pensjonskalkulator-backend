package no.nav.pensjon.kalkulator.simulering.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.asSpec
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class SimuleringController(private val simuleringClient: SimuleringClient,
                           private val pidGetter: PidGetter) {

    @PostMapping("alderspensjon/simulering")
    @Operation(
        summary = "Simuler alderspensjon",
        description = "Lag en prognose for framtidig alderspensjon"
    )
    fun simulerAlderspensjon(@RequestBody specDto: SimuleringSpecDto): Simuleringsresultat {
        val spec = asSpec(specDto, pidGetter.pid())
        return simuleringClient.simulerAlderspensjon(spec)
    }
}
