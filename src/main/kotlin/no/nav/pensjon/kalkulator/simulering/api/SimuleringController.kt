package no.nav.pensjon.kalkulator.simulering.api

import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecDto
import org.springframework.web.bind.annotation.*
import java.lang.System.currentTimeMillis

@RestController
@RequestMapping("api")
class SimuleringController(private val simuleringService: SimuleringService) {

    private val log = KotlinLogging.logger {}

    @PostMapping("alderspensjon/simulering")
    @Operation(
        summary = "Simuler alderspensjon",
        description = "Lag en prognose for framtidig alderspensjon"
    )
    fun simulerAlderspensjon(@RequestBody spec: SimuleringSpecDto): Simuleringsresultat {
        return timed(simuleringService::simulerAlderspensjon, spec, "alderspensjon/simulering")
    }

    private fun <A, R> timed(function : (A) -> R, argument: A, functionName: String): R {
        val startTimeMillis = currentTimeMillis()
        val result = function(argument)
        log.info { "$functionName took ${currentTimeMillis() - startTimeMillis} ms to process" }
        return result
    }
}
