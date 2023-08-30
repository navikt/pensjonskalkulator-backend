package no.nav.pensjon.kalkulator.uttaksalder.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.uttaksalder.Uttaksalder
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderService
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderSpecDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api")
class UttaksalderController(private val uttaksalderService: UttaksalderService) {

    @PostMapping("tidligste-uttaksalder")
    @Operation(
        summary = "Finn tidligste mulige uttaksalder",
        description = "Finn tidligste mulige uttaksalder",
    )
    fun finnTidligsteUttaksalder(@RequestBody spec: UttaksalderSpecDto?): Uttaksalder? {
        try {
            return uttaksalderService.finnTidligsteUttaksalder(spec ?: UttaksalderSpecDto.empty())
        } catch (e: EgressException) {
            throw ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Feil ved bestemmelse av tidligste mulige uttaksalder",
                e
            )
        }
    }
}
