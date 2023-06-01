package no.nav.pensjon.kalkulator

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.grunnbeloep.Grunnbeloep
import no.nav.pensjon.kalkulator.grunnbeloep.client.GrunnbeloepClient
import no.nav.pensjon.kalkulator.grunnbeloep.client.GrunnbeloepSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDate

@RestController
@RequestMapping("api")
class KalkulatorController(
    private val grunnbeloepClient: GrunnbeloepClient
) {
    @GetMapping("pensjonsberegning")
    @Operation(
        summary = "Beregn alderspensjon",
        description = "Beregn alderspensjon"
    )
    fun beregnAlderspensjon(): List<Simuleringsresultat> {
        return listOf(
            Simuleringsresultat(2024, BigDecimal("300001"), 67),
            Simuleringsresultat(2025, BigDecimal("300002"), 68),
            Simuleringsresultat(2026, BigDecimal("300003"), 69)
        )
    }

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

    @GetMapping("status")
    @Operation(summary = "Sjekk status", description = "Hent status for applikasjonens helsetilstand")
    fun status(): String {
        return """{ "status": "OK" }"""
    }
}
