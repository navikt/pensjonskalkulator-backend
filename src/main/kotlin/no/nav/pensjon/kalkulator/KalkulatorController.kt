package no.nav.pensjon.kalkulator

import no.nav.pensjon.kalkulator.grunnbeloep.GrunnbeloepClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class KalkulatorController(private val grunnbeloepClient: GrunnbeloepClient) {

    @GetMapping("api/kalkuler")
    fun kalkuler(): String = """{ "pensjon": 0 }"""

    @GetMapping("api/grunnbeloep")
    fun getGrunnbeloep(): String {
        return grunnbeloepClient.getGrunnbeloep("{\"fom\":1676042011910,\"tom\":1676042011910}").satsResultater.toString()
    }
}
