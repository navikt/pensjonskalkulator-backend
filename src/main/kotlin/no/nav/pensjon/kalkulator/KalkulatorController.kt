package no.nav.pensjon.kalkulator

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.grunnbeloep.Grunnbeloep
import no.nav.pensjon.kalkulator.grunnbeloep.client.GrunnbeloepClient
import no.nav.pensjon.kalkulator.grunnbeloep.client.GrunnbeloepSpec
import no.nav.pensjon.kalkulator.tech.security.egress.maskinporten.dev.SimulatorDevClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("api")
class KalkulatorController(
    private val grunnbeloepClient: GrunnbeloepClient,
    private val simulatorClient: SimulatorDevClient
) {
    @GetMapping("grunnbeloep")
    @Operation(
        summary = "Hent grunnbeløp",
        description = "Hent grunnbeløpet i folketrygden (G) for nåværende tidspunkt"
    )
    fun getGrunnbeloep(): Grunnbeloep {
        val now = LocalDate.now()
        val spec = GrunnbeloepSpec(now, now)
        return grunnbeloepClient.getGrunnbeloep(spec)
    }

    @GetMapping("krakend-status-test")
    @Operation(
        summary = "KrakenD status-test"
    )
    fun krakendStatusTest(): String = simulatorClient.status()

    @GetMapping("krakend-tmu-test")
    @Operation(
        summary = "KrakenD TMU-test"
    )
    fun krakendTidligstMuligUttak(): String = simulatorClient.tidligstMuligUttak()
}
