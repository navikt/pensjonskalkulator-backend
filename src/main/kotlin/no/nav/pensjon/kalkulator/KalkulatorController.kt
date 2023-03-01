package no.nav.pensjon.kalkulator

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.grunnbeloep.GrunnbeloepClient
import no.nav.pensjon.kalkulator.tech.security.egress.SecurityContextEnricher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    fun getGrunnbeloep(): String {
        securityContextEnricher.enrichAuthentication()
        return grunnbeloepClient.getGrunnbeloep("{\"fom\":1676042011910,\"tom\":1676042011910}").satsResultater.toString()
    }

    @GetMapping("status")
    @Operation(summary = "Sjekk status", description = "Hent status for applikasjonens helsetilstand")
    fun status(): String {
        return """{ "status": "OK" }"""
    }
}
