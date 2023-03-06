package no.nav.pensjon.kalkulator

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.grunnbeloep.Grunnbeloep
import no.nav.pensjon.kalkulator.grunnbeloep.client.GrunnbeloepClient
import no.nav.pensjon.kalkulator.grunnbeloep.client.GrunnbeloepSpec
import no.nav.pensjon.kalkulator.tech.security.egress.SecurityContextEnricher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("api")
class KalkulatorController(
    private val grunnbeloepClient: GrunnbeloepClient,
    private val securityContextEnricher: SecurityContextEnricher
) {
    @GetMapping("kalkuler")
    fun kalkuler(): String {
        return """{ "pensjon": 0 }"""
    }

    @GetMapping("grunnbeloep")
    @Operation(
        summary = "Hent grunnbeløp",
        description = "Hent grunnbeløpet i folketrygden (G) for nåværende tidspunkt"
    )
    fun getGrunnbeloep(): Grunnbeloep {
        securityContextEnricher.enrichAuthentication()
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
