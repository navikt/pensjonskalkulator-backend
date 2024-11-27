package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map.TjenestepensjonSimuleringMapper
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
        summary = "Simuler offentlig tjenestepensjon hos tp-leverandør bruker er medlem av",
        description = "Simulerer offentlig tjenestepensjon hos tp-leverandør som har ansvar for brukers tjenestepensjon"
    )
    fun simulerOffentligTjenestepensjon(@RequestBody spec: IngressSimuleringOFTPSpecV1): OFTPSimuleringsresultatDto {
        traceAid.begin()
        log.debug { "Request for simuler Offentlig tjenestepensjon" }

        return try {
            TjenestepensjonSimuleringMapper.toDto(timed(service::hentTjenestepensjonSimulering, spec, "simulerOffentligTjenestepensjon"))
                .also { log.debug { "Simuler Offentlig tjenestepensjon respons: $it" } }
        } catch (e: EgressException) {
            handleError(e)!!
        } finally {
            traceAid.end()
        }
    }

    @PostMapping("v1/simuler-oftp")
    @Operation(
        summary = "Simuler offentlig tjenestepensjon hos tp-leverandør bruker er medlem av",
        description = "Simulerer offentlig tjenestepensjon hos tp-leverandør som har ansvar for brukers tjenestepensjon"
    )
    fun simulerOffentligTjenestepensjonV1(@RequestBody spec: IngressSimuleringOFTPSpecV1): OFTPSimuleringsresultatDto {
        traceAid.begin()
        log.debug { "Request for simuler Offentlig tjenestepensjon" }

        return try {
            OFTPSimuleringsresultatDto(
                simuleringsresultatStatus = SimuleringsresultatStatus.OK,
                muligeTpLeverandoerListe = listOf("Statens pensjonskasse"),
                simulertTjenestepensjon = SimulertTjenestepensjon(
                    tpLeverandoer = "Statens pensjonskasse",
                    simuleringsresultat = Simuleringsresultat(
                        betingetTjenestepensjonErInkludert = true,
                        utbetalingsperioder = IntRange(spec.uttaksalder.aar, spec.uttaksalder.aar + 15)
                            .map { UtbetalingPerAar(aar = it, beloep = 57000) }
                    )
                )
            )
        } catch (e: EgressException) {
            handleError(e)!!
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved simulering av tjenestepensjon"
    }
}
