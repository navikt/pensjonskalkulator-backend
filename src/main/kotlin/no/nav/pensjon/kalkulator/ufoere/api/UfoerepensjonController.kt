package no.nav.pensjon.kalkulator.ufoere.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.tech.time.Timed
import no.nav.pensjon.kalkulator.ufoere.UfoerepensjonService
import no.nav.pensjon.kalkulator.ufoere.api.dto.UfoerepensjonDto
import no.nav.pensjon.kalkulator.ufoere.api.dto.UfoerepensjonSpecDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class UfoerepensjonController(private val service: UfoerepensjonService) : Timed() {

    @PostMapping("ufoerepensjon")
    @Operation(
        summary = "Har løpende uførepensjon",
        description = "Hvorvidt den innloggede brukeren har løpende uførepensjon"
    )
    fun harUfoerepensjon(@RequestBody spec: UfoerepensjonSpecDto): UfoerepensjonDto {
        return toDto(timed(service::harLoependeUfoerepensjon, spec.fom, "harLoependeUfoerepensjon"))
    }

    private companion object {
        private fun toDto(harUfoerepensjon: Boolean) = UfoerepensjonDto(harUfoerepensjon)
    }
}
