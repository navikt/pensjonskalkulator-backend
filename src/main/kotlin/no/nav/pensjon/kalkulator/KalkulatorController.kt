package no.nav.pensjon.kalkulator

import no.nav.pensjon.kalkulator.grunnbeloep.GrunnbeloepClient
import no.nav.pensjon.kalkulator.tech.security.egress.SecurityContextEnricher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class KalkulatorController(
    private val grunnbeloepClient: GrunnbeloepClient,
    private val securityContextEnricher: SecurityContextEnricher
) {

    @GetMapping("api/kalkuler")
    fun kalkuler(): String {
        return """{ "pensjon": 0 }"""
    }

    @GetMapping("api/grunnbeloep")
    fun getGrunnbeloep(): String {
        securityContextEnricher.enrichAuthentication()
        return grunnbeloepClient.getGrunnbeloep("{\"fom\":1676042011910,\"tom\":1676042011910}").satsResultater.toString()
    }

    @GetMapping("api/status")
    fun status(): String {
        return """{ "status": "OK" }"""
    }
}
