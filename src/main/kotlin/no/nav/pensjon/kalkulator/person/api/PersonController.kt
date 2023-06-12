package no.nav.pensjon.kalkulator.person.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.api.dto.PersonDto
import no.nav.pensjon.kalkulator.person.api.map.PersonMapper.toDto
import no.nav.pensjon.kalkulator.tech.time.Timed
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class PersonController(private val service: PersonService) : Timed() {

    @GetMapping("person")
    @Operation(
        summary = "Hent personinformasjon",
        description = "Henter personinformasjon om den innloggede brukeren"
    )
    fun person(): PersonDto = toDto(timed(service::getPerson, "person"))
}
