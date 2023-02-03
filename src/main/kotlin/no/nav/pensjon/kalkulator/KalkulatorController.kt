package no.nav.pensjon.kalkulator

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class KalkulatorController {

    @GetMapping("api/kalkuler")
    fun kalkuler(): String = """{ "pensjon": 0 }"""
}
