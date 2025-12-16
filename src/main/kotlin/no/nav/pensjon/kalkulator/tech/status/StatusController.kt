package no.nav.pensjon.kalkulator.tech.status

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.constraints.NotNull
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class StatusController {

    @GetMapping("status")
    @Operation(summary = "Sjekk status", description = "Hent status for applikasjonens helsetilstand")
    fun status() = ApiStatusDto("OK")
}

data class ApiStatusDto(
    @field:NotNull val status: String
)
