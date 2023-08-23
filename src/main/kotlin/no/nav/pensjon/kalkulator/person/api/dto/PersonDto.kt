package no.nav.pensjon.kalkulator.person.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import java.time.LocalDate

data class PersonDto(
    val fornavn: String?,
    val foedselsdato: LocalDate?,
    val sivilstand: Sivilstand?
)
