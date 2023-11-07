package no.nav.pensjon.kalkulator.person.api.dto

import java.time.LocalDate

data class ApiPersonDto(
    val fornavn: String,
    val foedselsdato: LocalDate,
    val sivilstand: ApiSivilstand
)
