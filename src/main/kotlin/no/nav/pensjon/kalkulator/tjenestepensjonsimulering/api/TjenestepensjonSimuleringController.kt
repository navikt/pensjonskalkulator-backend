package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api

import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.TjenestepensjonSimuleringService
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map.TjenestepensjonSimuleringResultMapperV1
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map.TjenestepensjonSimuleringSpecMapperV1.fromDto
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

    @PostMapping("v1/simuler-oftp")
    @Operation(
        summary = "Simuler offentlig tjenestepensjon hos tp-leverandør bruker er medlem av",
        description = "Simulerer offentlig tjenestepensjon hos tp-leverandør som har ansvar for brukers tjenestepensjon"
    )
    fun simulerOffentligTjenestepensjonV1(@RequestBody spec: IngressSimuleringOffentligTjenestepensjonSpecV1): OffentligTjenestepensjonSimuleringsresultatDtoV1 {
        traceAid.begin()
        log.debug { "Request for simuler Offentlig tjenestepensjon V1" }

        return try {
            TjenestepensjonSimuleringResultMapperV1.toDto(timed(service::hentTjenestepensjonSimulering, fromDto(spec), "simulerOffentligTjenestepensjon"))
                .also { log.debug { "Simuler Offentlig tjenestepensjon respons: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved simulering av tjenestepensjon"
    }
}
