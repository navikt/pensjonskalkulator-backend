package no.nav.pensjon.kalkulator

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.tech.security.egress.maskinporten.dev.SimulatorDevClient
import no.nav.pensjon.kalkulator.tech.security.egress.maskinporten.dev.TmuResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class KalkulatorController(
    private val simulatorClient: SimulatorDevClient
) {
    @GetMapping("simulator-status-test")
    @Operation(
        summary = "Simulator status-test"
    )
    fun simulatorStatusTest(): String = simulatorClient.status()

    @GetMapping("simulator-tmu-test")
    @Operation(
        summary = "Simulator TMU-test"
    )
    fun simulatorTidligstMuligUttakTest(): TmuResult? = simulatorClient.tidligstMuligUttak()

    @GetMapping("simulator-ap-test")
    @Operation(
        summary = "Simulator simuler-alderspensjon-test"
    )
    fun simulatorAlderspensjonTest(): String? = simulatorClient.alderspensjon()

    @GetMapping("simulator-sfb-test")
    @Operation(
        summary = "Simulator simuler-folketrygdbeholdning-test"
    )
    fun simulatorFolketrygdbeholdningTest(): String? = simulatorClient.folketrygdbeholdning()
}
