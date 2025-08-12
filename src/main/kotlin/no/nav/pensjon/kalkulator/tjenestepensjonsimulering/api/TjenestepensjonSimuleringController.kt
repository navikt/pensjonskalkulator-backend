package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api

import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.TjenestepensjonSimuleringService
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.SimuleringOffentligTjenestepensjonSpecV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.OffentligTjenestepensjonSimuleringResultV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map.TjenestepensjonSimuleringResultMapperV2.toDtoV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map.TjenestepensjonSimuleringSpecMapperV2.fromDtoV2
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class TjenestepensjonSimuleringController(
    val service: TjenestepensjonSimuleringService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v2/simuler-oftp")
    @Operation(
        summary = "Simuler offentlig tjenestepensjon hos TP-leverandør bruker er medlem av",
        description = "Simulerer offentlig tjenestepensjon hos TP-leverandør som har ansvar for brukers tjenestepensjon"
    )
    fun simulerOffentligTjenestepensjonV2(@RequestBody spec: SimuleringOffentligTjenestepensjonSpecV2): OffentligTjenestepensjonSimuleringResultV2 {
        traceAid.begin()
        log.debug { "Request for simuler offentlig tjenestepensjon V2: $spec" }

        return try {
            toDtoV2(timed(service::hentTjenestepensjonSimulering, fromDtoV2(spec), "simulerOffentligTjenestepensjon"))
                .also { log.debug { "Simuler offentlig tjenestepensjon respons: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V2")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved simulering av tjenestepensjon"
    }
}
