package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api

import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.TjenestepensjonSimuleringFoer1963Service
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.OffentligTjenestepensjonSimuleringFoer1963ResultV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.SimuleringOffentligTjenestepensjonFoer1963SpecV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.map.TjenestepensjonSimuleringFoer1963ResultMapperV2.toDtoV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.map.TjenestepensjonSimuleringFoer1963SpecMapperV2.fromDtoV2
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class TjenestepensjonSimuleringFoer1963Controller(
    val service: TjenestepensjonSimuleringFoer1963Service,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v2/simuler-oftp/foer-1963")
    @Operation(
        summary = "Simuler offentlig tjenestepensjon hos TP-leverandør bruker er medlem av",
        description = "Simulerer offentlig tjenestepensjon hos TP-leverandør som har ansvar for brukers tjenestepensjon"
    )
    fun simulerOffentligTjenestepensjonFoer1963V1(@RequestBody spec: SimuleringOffentligTjenestepensjonFoer1963SpecV2): OffentligTjenestepensjonSimuleringFoer1963ResultV2 {
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
