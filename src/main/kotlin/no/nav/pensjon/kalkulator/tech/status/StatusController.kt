package no.nav.pensjon.kalkulator.tech.status

import io.swagger.v3.oas.annotations.Operation
import org.intellij.lang.annotations.Language
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class StatusController {

    @GetMapping("status")
    @Operation(summary = "Sjekk status", description = "Hent status for applikasjonens helsetilstand")
    @Language("json")
    fun status() = """{ "status": "OK" }"""
}
