package no.nav.pensjon.kalkulator.uttaksalder.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.tech.time.Timed
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
class UttaksalderController(private val service: UttaksalderService) : Timed() {

    @PostMapping("tidligste-uttaksalder")
    @Operation(
        summary = "Tidligste mulige uttaksalder",
        description = "Finn tidligste mulige uttaksalder for innlogget bruker",
    )
    fun finnTidligsteUttaksalder(@RequestBody spec: UttaksalderSpecDto?): Uttaksalder? {
        try {
            return timed(
                service::finnTidligsteUttaksalder,
                spec ?: UttaksalderSpecDto.empty(),
                "finnTidligsteUttaksalder"
            )
        } catch (e: EgressException) {
            throw ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Feil ved bestemmelse av tidligste mulige uttaksalder: ${extractMessageRecursively(e)}",
                e
            )
        }
    }
}
